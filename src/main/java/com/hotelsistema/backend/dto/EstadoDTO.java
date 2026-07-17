package com.hotelsistema.backend.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoDTO(
        @NotNull(message = "El estado (activo/inactivo) es obligatorio")
        Boolean activo
) {
}