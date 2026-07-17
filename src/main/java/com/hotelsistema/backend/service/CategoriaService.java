package com.hotelsistema.backend.service;

import com.hotelsistema.backend.dto.categoriaDTO.CategoriaRequest;
import com.hotelsistema.backend.dto.categoriaDTO.CategoriaResponse;
import com.hotelsistema.backend.exception.RecursoDuplicadoException;
import com.hotelsistema.backend.exception.RecursoNoEncontradoException;
import com.hotelsistema.backend.model.Categoria;
import com.hotelsistema.backend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RecursoDuplicadoException("Ya existe una categoria llamada '" + request.nombre() + "'");
        }

        Categoria categoria = Categoria.builder()
                .nombre(request.nombre())
                .activo(true)
                .build();

        categoria = categoriaRepository.save(categoria);
        return CategoriaResponse.fromEntity(categoria);
    }

    public List<CategoriaResponse> listarActivas() {
        return categoriaRepository.findByActivoTrue().stream()
                .map(CategoriaResponse::fromEntity)
                .toList();
    }

    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(CategoriaResponse::fromEntity)
                .toList();
    }

    public CategoriaResponse obtenerPorId(Integer id) {
        return CategoriaResponse.fromEntity(buscarOFallar(id));
    }

    public CategoriaResponse actualizar(Integer id, CategoriaRequest request) {
        Categoria categoria = buscarOFallar(id);

        boolean cambioDeNombre = !categoria.getNombre().equalsIgnoreCase(request.nombre());
        if (cambioDeNombre && categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RecursoDuplicadoException("Ya existe una categoria llamada '" + request.nombre() + "'");
        }

        categoria.setNombre(request.nombre());
        categoria = categoriaRepository.save(categoria);
        return CategoriaResponse.fromEntity(categoria);
    }

    public void cambiarEstado(Integer id, Boolean nuevoEstado) {
        Categoria categoria = buscarOFallar(id);
        categoria.setActivo(nuevoEstado);
        categoriaRepository.save(categoria);
    }

    Categoria buscarOFallar(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una categoria con id " + id));
    }
}
