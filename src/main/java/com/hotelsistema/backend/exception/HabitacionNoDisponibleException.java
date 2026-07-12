package com.hotelsistema.backend.exception;

public class HabitacionNoDisponibleException extends RuntimeException {
    public HabitacionNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
