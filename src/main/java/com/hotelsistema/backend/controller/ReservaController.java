package com.hotelsistema.backend.controller;

import com.hotelsistema.backend.dto.reservaDTO.CambiarEstadoReservaRequest;
import com.hotelsistema.backend.dto.reservaDTO.CrearReservaRequest;
import com.hotelsistema.backend.dto.reservaDTO.ReservaResponse;
import com.hotelsistema.backend.security.UserPrincipal;
import com.hotelsistema.backend.service.ReservaService;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Gestión de Reservas", description = "Endpoints para la creación, consulta y administración de las reservas de habitaciones y consumos asociados.")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(summary = "Crear una nueva reserva", 
               description = "Permite a un CLIENTE reservar para sí mismo. El staff (ADMINISTRADOR/RECEPCIONISTA) puede reservar a nombre de un cliente enviando el usuarioId en el body.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o fechas no disponibles"),
            @ApiResponse(responseCode = "404", description = "Habitación o usuario no encontrado")
    })
    public ResponseEntity<ReservaResponse> crear(
            @Valid @RequestBody CrearReservaRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        ReservaResponse response = reservaService.crear(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mias")
    @Operation(summary = "Listar mis reservas", 
               description = "Devuelve el historial completo de reservas pertenecientes al usuario actualmente autenticado (Cliente).")
    @ApiResponse(responseCode = "200", description = "Lista de reservas devuelta con éxito")
    public ResponseEntity<List<ReservaResponse>> listarMias(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reservaService.listarMias(principal.getId()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar todas las reservas (Staff)", 
               description = "Obtiene el registro de todas las reservas del hotel. Acceso exclusivo para el personal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "403", description = "No autorizado (Solo Staff)")
    })
    public ResponseEntity<List<ReservaResponse>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/habitacion/{habitacionId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Historial de reservas por habitación", 
               description = "Útil para reportes de ocupación. Devuelve todas las reservas vinculadas a una habitación específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial devuelto con éxito"),
            @ApiResponse(responseCode = "404", description = "Habitación no encontrada")
    })
    public ResponseEntity<List<ReservaResponse>> listarPorHabitacion(
            @Parameter(description = "ID de la habitación a consultar", required = true) @PathVariable Integer habitacionId) {
        return ResponseEntity.ok(reservaService.listarPorHabitacion(habitacionId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalles de una reserva", 
               description = "El cliente solo puede consultar su propia reserva. El staff puede consultar cualquier reserva del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles de la reserva obtenidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Intento de ver una reserva ajena)"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaResponse> obtenerPorId(
            @Parameter(description = "ID de la reserva", required = true) @PathVariable Integer id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id, principal));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Anular una reserva", 
               description = "El cliente puede cancelar su reserva si aún está en estado PENDIENTE. El staff puede cancelar cualquier reserva que no esté FINALIZADA o ya ANULADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva anulada con éxito"),
            @ApiResponse(responseCode = "400", description = "La reserva ya no puede ser cancelada debido a su estado actual"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<ReservaResponse> cancelar(
            @Parameter(description = "ID de la reserva a cancelar", required = true) @PathVariable Integer id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reservaService.cancelar(id, principal));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Cambiar estado de una reserva (Staff)", 
               description = "Operación operativa para que el staff marque una reserva como PAGADA o FINALIZADA (Checkout).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Estado inválido o transición no permitida"),
            @ApiResponse(responseCode = "403", description = "No autorizado (Solo Staff)")
    })
    public ResponseEntity<ReservaResponse> cambiarEstado(
            @Parameter(description = "ID de la reserva a actualizar", required = true) @PathVariable Integer id,
            @Valid @RequestBody CambiarEstadoReservaRequest request) {
        return ResponseEntity.ok(reservaService.cambiarEstado(id, request));
    }
}