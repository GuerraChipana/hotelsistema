package com.hotelsistema.backend.dto.reservaDTO;

import com.hotelsistema.backend.model.EstadoReserva;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoReservaRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoReserva estado
) {
}
