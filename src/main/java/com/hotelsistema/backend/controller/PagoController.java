package com.hotelsistema.backend.controller;

import com.hotelsistema.backend.dto.pagoDTO.PagoResponse;
import com.hotelsistema.backend.dto.pagoDTO.ProcesarPagoRequest;
import com.hotelsistema.backend.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'CLIENTE')")
    public ResponseEntity<PagoResponse> procesarPago(@Valid @RequestBody ProcesarPagoRequest request) {
        // Un cliente puede pagarse a sí mismo por Yape/Tarjeta, o recepción cobra en efectivo
        return new ResponseEntity<>(pagoService.procesarPago(request), HttpStatus.CREATED);
    }

    @GetMapping("/reserva/{reservaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'CLIENTE')")
    public ResponseEntity<List<PagoResponse>> obtenerPagosDeReserva(@PathVariable Integer reservaId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorReserva(reservaId));
    }
}