package com.hotelsistema.backend.dto.habitacionesDTO;

import com.hotelsistema.backend.model.TipoHabitacion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CrearHabitacionRequest(

        @NotBlank(message = "El numero de habitacion es obligatorio")
        @Size(max = 10)
        String numeroHabitacion,

        @NotNull(message = "El tipo de habitacion es obligatorio")
        TipoHabitacion tipo,

        @NotNull(message = "El precio por dia es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio por dia debe ser mayor a 0")
        BigDecimal precioDia,

        @NotNull(message = "El precio por hora es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio por hora debe ser mayor a 0")
        BigDecimal precioHora,

        String imagenUrl
) {
}
