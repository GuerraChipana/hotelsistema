package com.hotelsistema.backend.exception;

public class EmailYaRegistradoException extends RuntimeException {
    public EmailYaRegistradoException(String email) {
        super("Ya existe una cuenta registrada con el email: " + email);
    }
}
