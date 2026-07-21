package com.hotelsistema.backend.controller;

import com.hotelsistema.backend.dto.authDTO.CrearStaffRequest;
import com.hotelsistema.backend.dto.authDTO.RegisterRequest;
import com.hotelsistema.backend.dto.usuarioDTO.UsuarioResponse;
import com.hotelsistema.backend.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@Tag(name = "Gestión de Usuarios (Admin/Staff)", description = "Endpoints para la administración de cuentas de empleados y el registro rápido de clientes en mostrador.")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear cuenta de Staff", 
               description = "Registra un nuevo usuario con privilegios elevados (ADMINISTRADOR o RECEPCIONISTA). Acceso exclusivo para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta de staff creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, rol no permitido, o el email ya está registrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Solo Administradores)")
    })
    public ResponseEntity<UsuarioResponse> crearStaff(
            @Valid @RequestBody CrearStaffRequest request) {
        UsuarioResponse response = usuarioService.crearStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/clientes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Crear cliente presencial (Walk-in)", 
               description = "Permite al personal del hotel registrar rápidamente a un cliente que llega sin cuenta previa. Se le asignará el rol CLIENTE automáticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o el email ya está en uso"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Solo Staff)")
    })
    public ResponseEntity<UsuarioResponse> crearClientePresencial(
            @Valid @RequestBody RegisterRequest request) {
        UsuarioResponse response = usuarioService.crearClientePresencial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar todos los usuarios", 
               description = "Devuelve el directorio completo de usuarios registrados en el sistema (Clientes y Staff). Útil para búsquedas en recepción.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Solo Staff)")
    })
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener usuario por ID", 
               description = "Busca y devuelve los detalles de un usuario específico mediante su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles del usuario obtenidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Solo Staff)"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponse> obtenerPorId(
            @Parameter(description = "ID del usuario a consultar", required = true) @PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }
}