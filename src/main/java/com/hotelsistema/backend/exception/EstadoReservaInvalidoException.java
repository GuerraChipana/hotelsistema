package com.hotelsistema.backend.exception;

public class EstadoReservaInvalidoException extends RuntimeException {
    public EstadoReservaInvalidoException(String mensaje) {
        super(mensaje);
    }
}
