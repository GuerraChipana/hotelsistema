package com.hotelsistema.backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ModalidadReservaConverter implements AttributeConverter<ModalidadReserva, String> {

    @Override
    public String convertToDatabaseColumn(ModalidadReserva modalidad) {
        return modalidad == null ? null : modalidad.name().toLowerCase();
    }

    @Override
    public ModalidadReserva convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ModalidadReserva.valueOf(dbData.toUpperCase());
    }
}
