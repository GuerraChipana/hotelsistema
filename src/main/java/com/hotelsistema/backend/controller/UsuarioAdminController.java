package com.hotelsistema.backend.controller;

import com.hotelsistema.backend.dto.authDTO.CrearStaffRequest;
import com.hotelsistema.backend.dto.usuarioDTO.UsuarioResponse;
import com.hotelsistema.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping; // <-- Importante
import org.springframework.web.bind.annotation.PathVariable; // <-- Importante
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List; // <-- Importante

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    /**
     * Solo un ADMINISTRADOR autenticado puede crear cuentas de staff
     * (RECEPCIONISTA o ADMINISTRADOR).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> crearStaff(@Valid @RequestBody CrearStaffRequest request) {
        UsuarioResponse response = usuarioService.crearStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- NUEVOS ENDPOINTS AÑADIDOS ---

    /**
     * Listar todos los usuarios del sistema (Clientes, Recepcionistas y Administradores)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    /**
     * Obtener el detalle de un usuario específico por su ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }
}