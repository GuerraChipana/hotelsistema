package com.hotelsistema.backend.exception;

/**
 * Generica y reutilizable: la usaremos tambien en Producto, Reserva, etc.
 * Evita crear una excepcion "NoEncontrada" por cada entidad.
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
