package com.hotelsistema.backend.repository;

import com.hotelsistema.backend.model.EstadoReserva;
import com.hotelsistema.backend.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByUsuarioIdOrderByFechaHoraEntradaDesc(Integer usuarioId);

    List<Reserva> findByHabitacionIdOrderByFechaHoraEntradaDesc(Integer habitacionId);

    List<Reserva> findAllByOrderByFechaHoraEntradaDesc();

    /**
     * El corazon de las reservas: busca si YA existe alguna reserva (que no
     * este anulada) para la misma habitacion cuyo rango de fechas se cruce
     * con el nuevo rango solicitado.
     * <p>
     * Dos rangos [A,B) y [C,D) se cruzan si A < D Y C < B. Por eso las dos
     * condiciones de fecha de abajo.
     * <p>
     * idAExcluir se usa cuando estamos verificando el solapamiento de una
     * reserva que ya existe (por ahora no se usa, pero queda listo para
     * cuando se agregue "editar fechas de una reserva").
     */
    @Query("""
            SELECT r FROM Reserva r
            WHERE r.habitacion.id = :habitacionId
              AND r.estado NOT IN :estadosExcluidos
              AND r.fechaHoraEntrada < :nuevaSalida
              AND r.fechaHoraSalida > :nuevaEntrada
              AND (:idAExcluir IS NULL OR r.id <> :idAExcluir)
            """)
    List<Reserva> findSolapamientos(@Param("habitacionId") Integer habitacionId,
                                     @Param("nuevaEntrada") LocalDateTime nuevaEntrada,
                                     @Param("nuevaSalida") LocalDateTime nuevaSalida,
                                     @Param("estadosExcluidos") List<EstadoReserva> estadosExcluidos,
                                     @Param("idAExcluir") Integer idAExcluir);
                                     
    // AGREGAR ESTE MÉTODO TAMBIÉN PARA EL SCHEDULER (Lo usaremos en el paso 3)
    List<Reserva> findByEstadoAndFechaHoraEntradaBefore(EstadoReserva estado, LocalDateTime fechaHora);
}
