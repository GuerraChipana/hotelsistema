package com.hotelsistema.backend.dto.reservaDTO;

import com.hotelsistema.backend.model.ModalidadReserva;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * OJO: aqui NO van costo_habitacion, costo_piscina ni total_general.
 * Esos se calculan siempre en el servidor (ReservaService) a partir del
 * precio real de la habitacion — nunca confiamos en un precio que mande
 * el cliente, o cualquiera podria reservar pagando lo que quiera.
 */
public record CrearReservaRequest(

        // Solo se usa cuando quien llama es ADMINISTRADOR o RECEPCIONISTA
        // y esta reservando a nombre de un cliente (walk-in). Si el que llama
        // es un CLIENTE, este campo se ignora y se usa su propio id.
        Integer usuarioId,

        @NotNull(message = "La habitacion es obligatoria")
        Integer habitacionId,

        @NotNull(message = "La modalidad es obligatoria (POR_DIA o POR_HORAS)")
        ModalidadReserva modalidad,

        @NotNull(message = "La fecha de entrada es obligatoria")
        @FutureOrPresent(message = "La fecha de entrada no puede ser en el pasado")
        LocalDateTime fechaHoraEntrada,

        @NotNull(message = "La fecha de salida es obligatoria")
        LocalDateTime fechaHoraSalida,

        @Min(value = 1, message = "Debe haber al menos 1 adulto")
        Integer cantAdultos,

        @Min(value = 0, message = "La cantidad de ninos no puede ser negativa")
        Integer cantNinos,

        Boolean accesoPiscina
) {
}
