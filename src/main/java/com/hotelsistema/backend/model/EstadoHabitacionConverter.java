package com.hotelsistema.backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoHabitacionConverter implements AttributeConverter<EstadoHabitacion, String> {

    @Override
    public String convertToDatabaseColumn(EstadoHabitacion estado) {
        return estado == null ? null : estado.name().toLowerCase();
    }

    @Override
    public EstadoHabitacion convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EstadoHabitacion.valueOf(dbData.toUpperCase());
    }
}
