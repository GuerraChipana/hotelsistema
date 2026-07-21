package com.hotelsistema.backend.controller;

import com.hotelsistema.backend.dto.EstadoDTO;
import com.hotelsistema.backend.dto.habitacionesDTO.ActualizarHabitacionRequest;
import com.hotelsistema.backend.dto.habitacionesDTO.CrearHabitacionRequest;
import com.hotelsistema.backend.dto.habitacionesDTO.HabitacionResponse;
import com.hotelsistema.backend.service.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService habitacionService;

    // Cualquier usuario autenticado (incluido CLIENTE) puede ver las habitaciones
    // activas -> necesario para que el cliente elija donde reservar.
    @GetMapping
    public ResponseEntity<List<HabitacionResponse>> listarActivas() {
        return ResponseEntity.ok(habitacionService.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(habitacionService.obtenerPorId(id));
    }

    // Incluye las inactivas (fuera de servicio) -> solo tiene sentido para gestion.
    @GetMapping("/admin/todas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<HabitacionResponse>> listarTodas() {
        return ResponseEntity.ok(habitacionService.listarTodas());
    }

    // Alta de inventario fisico -> solo el administrador gestiona esto.
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<HabitacionResponse> crear(@Valid @RequestBody CrearHabitacionRequest request) {
        HabitacionResponse response = habitacionService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Cambiar tipo/precios -> solo el administrador (afecta la facturacion).
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<HabitacionResponse> actualizar(@PathVariable Integer id,
                                                          @Valid @RequestBody ActualizarHabitacionRequest request) {
        return ResponseEntity.ok(habitacionService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoDTO estadoDTO) {
        habitacionService.cambiarEstado(id, estadoDTO.activo());
        return ResponseEntity.noContent().build();
    }
}
