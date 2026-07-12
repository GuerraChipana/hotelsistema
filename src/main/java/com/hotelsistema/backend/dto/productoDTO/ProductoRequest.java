package com.hotelsistema.backend.dto.productoDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductoRequest(

        @NotNull(message = "La categoria es obligatoria")
        Integer categoriaId,

        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 150)
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        BigDecimal precio,

        @Size(max = 255)
        String imagenUrl // opcional
) {
}
