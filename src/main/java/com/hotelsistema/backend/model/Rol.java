package com.hotelsistema.backend.model;

/**
 * Roles del sistema. Los valores se guardan en minusculas en la BD
 * (ver RolConverter), tal como esta definido el ENUM en la tabla `usuarios`.
 */
public enum Rol {
    ADMINISTRADOR,
    RECEPCIONISTA,
    CLIENTE
}
