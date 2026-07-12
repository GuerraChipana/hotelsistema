package com.hotelsistema.backend.dto.reservaDTO;

import com.hotelsistema.backend.model.EstadoReserva;
import com.hotelsistema.backend.model.ModalidadReserva;
import com.hotelsistema.backend.model.Reserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservaResponse(
        Integer id,
        Integer usuarioId,
        String usuarioNombre,
        Integer habitacionId,
        String habitacionNumero,
        ModalidadReserva modalidad,
        LocalDateTime fechaHoraEntrada,
        LocalDateTime fechaHoraSalida,
        Integer cantAdultos,
        Integer cantNinos,
        Boolean accesoPiscina,
        BigDecimal costoHabitacion,
        BigDecimal costoPiscina,
        BigDecimal costoProductos,
        BigDecimal totalGeneral,
        EstadoReserva estado,
        LocalDateTime fechaCreacion
) {
    public static ReservaResponse fromEntity(Reserva r) {
        return new ReservaResponse(
                r.getId(),
                r.getUsuario().getId(),
                r.getUsuario().getNombre() + " " + (r.getUsuario().getApellidos() != null ? r.getUsuario().getApellidos() : ""),
                r.getHabitacion().getId(),
                r.getHabitacion().getNumeroHabitacion(),
                r.getModalidad(),
                r.getFechaHoraEntrada(),
                r.getFechaHoraSalida(),
                r.getCantAdultos(),
                r.getCantNinos(),
                r.getAccesoPiscina(),
                r.getCostoHabitacion(),
                r.getCostoPiscina(),
                r.getCostoProductos(),
                r.getTotalGeneral(),
                r.getEstado(),
                r.getFechaCreacion()
        );
    }
}
