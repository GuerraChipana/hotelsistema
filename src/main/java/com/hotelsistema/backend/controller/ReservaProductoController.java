package com.hotelsistema.backend.controller;

import com.hotelsistema.backend.dto.reservaproductoDTO.AgregarProductoRequest;
import com.hotelsistema.backend.dto.reservaproductoDTO.ReservaProductoResponse;
import com.hotelsistema.backend.service.ReservaProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas/{reservaId}/productos")
@RequiredArgsConstructor
public class ReservaProductoController {

    private final ReservaProductoService reservaProductoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<ReservaProductoResponse> agregarProducto(
            @PathVariable Integer reservaId, // <-- CORREGIDO
            @Valid @RequestBody AgregarProductoRequest request) {
        return new ResponseEntity<>(reservaProductoService.agregarProducto(reservaId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReservaProductoResponse>> obtenerProductos(@PathVariable Integer reservaId) { // <-- CORREGIDO
        return ResponseEntity.ok(reservaProductoService.obtenerProductosDeReserva(reservaId));
    }

    @DeleteMapping("/{reservaProductoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Integer reservaId, // <-- CORREGIDO
            @PathVariable Integer reservaProductoId) { // <-- CORREGIDO
        reservaProductoService.eliminarProducto(reservaId, reservaProductoId);
        return ResponseEntity.noContent().build();
    }
}