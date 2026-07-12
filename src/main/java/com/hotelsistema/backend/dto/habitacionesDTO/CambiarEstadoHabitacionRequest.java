package com.hotelsistema.backend.dto.habitacionesDTO;

import com.hotelsistema.backend.model.EstadoHabitacion;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoHabitacionRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoHabitacion estado
) {
}
