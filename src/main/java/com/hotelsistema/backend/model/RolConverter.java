package com.hotelsistema.backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * La columna `rol` en MySQL es ENUM('administrador','recepcionista','cliente') -> minusculas.
 * En Java usamos la convencion ADMINISTRADOR/RECEPCIONISTA/CLIENTE -> mayusculas.
 * Este converter traduce automaticamente entre ambos mundos.
 */
@Converter(autoApply = true)
public class RolConverter implements AttributeConverter<Rol, String> {

    @Override
    public String convertToDatabaseColumn(Rol rol) {
        if (rol == null) {
            return null;
        }
        return rol.name().toLowerCase();
    }

    @Override
    public Rol convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Rol.valueOf(dbData.toUpperCase());
    }
}
