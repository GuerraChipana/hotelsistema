package com.hotelsistema.backend.dto.reservaproductoDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgregarProductoRequest {
    @NotNull(message = "El ID del producto es obligatorio")
    private Integer productoId; 

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;
}