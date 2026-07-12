package com.hotelsistema.backend.dto.productoDTO;

import com.hotelsistema.backend.model.Producto;

import java.math.BigDecimal;

public record ProductoResponse(
        Integer id,
        Integer categoriaId,
        String categoriaNombre,
        String nombre,
        BigDecimal precio,
        String imagenUrl,
        Boolean activo
) {
    public static ProductoResponse fromEntity(Producto producto) {
        // categoria puede ser null (ON DELETE SET NULL) -> el producto queda "sin categoria"
        Integer categoriaId = producto.getCategoria() != null ? producto.getCategoria().getId() : null;
        String categoriaNombre = producto.getCategoria() != null ? producto.getCategoria().getNombre() : null;

        return new ProductoResponse(
                producto.getId(), categoriaId, categoriaNombre,
                producto.getNombre(), producto.getPrecio(), producto.getImagenUrl(), producto.getActivo()
        );
    }
}
