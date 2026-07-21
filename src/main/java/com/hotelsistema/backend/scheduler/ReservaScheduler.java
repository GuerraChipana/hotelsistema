package com.hotelsistema.backend.scheduler;

import com.hotelsistema.backend.model.EstadoHabitacion;
import com.hotelsistema.backend.model.EstadoReserva;
import com.hotelsistema.backend.model.Reserva;
import com.hotelsistema.backend.repository.HabitacionRepository;
import com.hotelsistema.backend.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservaScheduler {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;

    // Se ejecuta automáticamente cada 30 minutos (1800000 ms)
    @Scheduled(fixedRate = 1800000)
    @Transactional
    public void liberarHabitacionesNoShow() {
        LocalDateTime ahora = LocalDateTime.now();
        
        // Busca reservas pendientes donde ya pasaron 2 horas de su hora de entrada prevista
        List<Reserva> vencidas = reservaRepository.findByEstadoAndFechaHoraEntradaBefore(
                EstadoReserva.PENDIENTE, ahora.minusHours(2));

        for (Reserva reserva : vencidas) {
            reserva.setEstado(EstadoReserva.ANULADA);
            log.info("Sistema: Reserva ID {} anulada automáticamente por No-Show.", reserva.getId());
            
            // Si por algún error la habitación estaba marcada como ocupada, se libera
            if (reserva.getHabitacion().getEstado() == EstadoHabitacion.OCUPADO) {
                reserva.getHabitacion().setEstado(EstadoHabitacion.LIBRE);
                habitacionRepository.save(reserva.getHabitacion());
            }
        }
        
        if (!vencidas.isEmpty()) {
            reservaRepository.saveAll(vencidas);
        }
    }
}