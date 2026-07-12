package com.hotelsistema.backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoReservaConverter implements AttributeConverter<EstadoReserva, String> {

    @Override
    public String convertToDatabaseColumn(EstadoReserva estado) {
        return estado == null ? null : estado.name().toLowerCase();
    }

    @Override
    public EstadoReserva convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EstadoReserva.valueOf(dbData.toUpperCase());
    }
}
