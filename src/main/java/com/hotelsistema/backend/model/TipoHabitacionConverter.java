package com.hotelsistema.backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoHabitacionConverter implements AttributeConverter<TipoHabitacion, String> {

    @Override
    public String convertToDatabaseColumn(TipoHabitacion tipo) {
        return tipo == null ? null : tipo.name().toLowerCase();
    }

    @Override
    public TipoHabitacion convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TipoHabitacion.valueOf(dbData.toUpperCase());
    }
}
