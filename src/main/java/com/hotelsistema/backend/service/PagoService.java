package com.hotelsistema.backend.service;

import com.hotelsistema.backend.dto.pagoDTO.PagoResponse;
import com.hotelsistema.backend.dto.pagoDTO.ProcesarPagoRequest;
import com.hotelsistema.backend.exception.RecursoNoEncontradoException;
import com.hotelsistema.backend.model.EstadoPago;
import com.hotelsistema.backend.model.MetodoPago;
import com.hotelsistema.backend.model.Pago;
import com.hotelsistema.backend.model.Reserva;
import com.hotelsistema.backend.repository.PagoRepository;
import com.hotelsistema.backend.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    @Transactional
    public PagoResponse procesarPago(ProcesarPagoRequest request) {
        Reserva reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + request.getReservaId()));

        // Convertimos el String (ej. "TARJETA_CREDITO") al Enum MetodoPago
        MetodoPago metodoEnum = MetodoPago.valueOf(request.getMetodoPago().trim().toUpperCase());
        
        EstadoPago estadoSimulado = simularPasarela(metodoEnum);

        Pago pago = Pago.builder()
                .reserva(reserva)
                .monto(request.getMonto())
                .metodoPago(metodoEnum)
                .estado(estadoSimulado)
                .build();

        Pago guardado = pagoRepository.save(pago);

        return mapearAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> obtenerPagosPorReserva(Integer reservaId) {
        if (!reservaRepository.existsById(reservaId)) {
            throw new RecursoNoEncontradoException("Reserva no encontrada con ID: " + reservaId);
        }
        return pagoRepository.findByReservaId(reservaId).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    private EstadoPago simularPasarela(MetodoPago metodoPago) {
        // En tu DB solo existen métodos digitales (tarjetas o Yape)
        // Les damos un 90% de probabilidad de éxito simulado
        Random random = new Random();
        int probabilidad = random.nextInt(100) + 1; 
        
        return probabilidad <= 90 ? EstadoPago.APROBADO : EstadoPago.RECHAZADO;
    }

    private PagoResponse mapearAResponse(Pago pago) {
        return PagoResponse.builder()
                .id(pago.getId())
                .reservaId(pago.getReserva().getId())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago().name()) // Convertimos el Enum a texto para enviarlo al cliente
                .estado(pago.getEstado().name())
                .fechaPago(pago.getFechaPago())
                .build();
    }
}