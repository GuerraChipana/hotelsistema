package com.hotelsistema.backend.dto.habitacionesDTO;

import com.hotelsistema.backend.model.EstadoHabitacion;
import com.hotelsistema.backend.model.Habitacion;
import com.hotelsistema.backend.model.TipoHabitacion;

import java.math.BigDecimal;

public record HabitacionResponse(
        Integer id,
        String numeroHabitacion,
        TipoHabitacion tipo,
        BigDecimal precioDia,
        BigDecimal precioHora,
        EstadoHabitacion estado,
        String imagenUrl,
        Boolean activo
) {
    public static HabitacionResponse fromEntity(Habitacion h) {
        return new HabitacionResponse(
                h.getId(), h.getNumeroHabitacion(), h.getTipo(),
                h.getPrecioDia(), h.getPrecioHora(), h.getEstado(), h.getImagenUrl(), h.getActivo()
        );
    }
}
