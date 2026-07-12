package com.hotelsistema.backend.controller;

import com.hotelsistema.backend.dto.reservaDTO.CambiarEstadoReservaRequest;
import com.hotelsistema.backend.dto.reservaDTO.CrearReservaRequest;
import com.hotelsistema.backend.dto.reservaDTO.ReservaResponse;
import com.hotelsistema.backend.security.UserPrincipal;
import com.hotelsistema.backend.service.ReservaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // CLIENTE reserva para si mismo. ADMINISTRADOR/RECEPCIONISTA pueden
    // reservar a nombre de un cliente mandando usuarioId en el body.
    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody CrearReservaRequest request,
                                                  @AuthenticationPrincipal UserPrincipal principal) {
        ReservaResponse response = reservaService.crear(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Cualquier usuario autenticado ve SUS propias reservas.
    @GetMapping("/mias")
    public ResponseEntity<List<ReservaResponse>> listarMias(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reservaService.listarMias(principal.getId()));
    }

    // Staff ve todas las reservas del hotel.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<ReservaResponse>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    // Historial de una habitacion especifica (util para reportes/ocupacion).
    @GetMapping("/habitacion/{habitacionId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<ReservaResponse>> listarPorHabitacion(@PathVariable Integer habitacionId) {
        return ResponseEntity.ok(reservaService.listarPorHabitacion(habitacionId));
    }

    // El propio cliente solo puede ver la suya; staff puede ver cualquiera
    // (la validacion de a quien pertenece vive en el servicio).
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> obtenerPorId(@PathVariable Integer id,
                                                         @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id, principal));
    }

    // El cliente puede cancelar la suya si sigue PENDIENTE; staff puede
    // cancelar cualquiera que no este ya FINALIZADA/ANULADA.
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Integer id,
                                                     @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reservaService.cancelar(id, principal));
    }

    // Marcar como PAGADA o FINALIZADA -> tarea operativa, solo staff.
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ReservaResponse> cambiarEstado(@PathVariable Integer id,
                                                          @Valid @RequestBody CambiarEstadoReservaRequest request) {
        return ResponseEntity.ok(reservaService.cambiarEstado(id, request));
    }
}
