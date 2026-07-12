package com.hotelsistema.backend.dto.categoriaDTO;

import com.hotelsistema.backend.model.Categoria;

public record CategoriaResponse(
        Integer id,
        String nombre,
        Boolean activo
) {
    public static CategoriaResponse fromEntity(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.getActivo());
    }
}
