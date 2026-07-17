package com.hotelsistema.backend.service;

import com.hotelsistema.backend.dto.productoDTO.ProductoRequest;
import com.hotelsistema.backend.dto.productoDTO.ProductoResponse;
import com.hotelsistema.backend.exception.RecursoNoEncontradoException;
import com.hotelsistema.backend.model.Categoria;
import com.hotelsistema.backend.model.Producto;
import com.hotelsistema.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // clave: Producto.categoria es LAZY y open-in-view esta en false,
// asi que la sesion de Hibernate tiene que seguir abierta mientras se arma el DTO.
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService; // reutiliza buscarOFallar() para la categoria

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = categoriaService.buscarOFallar(request.categoriaId());

        Producto producto = Producto.builder()
                .categoria(categoria)
                .nombre(request.nombre())
                .precio(request.precio())
                .imagenUrl(request.imagenUrl())
                .activo(true)
                .build();

        producto = productoRepository.save(producto);
        return ProductoResponse.fromEntity(producto);
    }

    public List<ProductoResponse> listarActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(ProductoResponse::fromEntity)
                .toList();
    }

    public List<ProductoResponse> listarActivosPorCategoria(Integer categoriaId) {
        return productoRepository.findByActivoTrueAndCategoriaId(categoriaId).stream()
                .map(ProductoResponse::fromEntity)
                .toList();
    }

    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream()
                .map(ProductoResponse::fromEntity)
                .toList();
    }

    public ProductoResponse obtenerPorId(Integer id) {
        return ProductoResponse.fromEntity(buscarOFallar(id));
    }

    @Transactional
    public ProductoResponse actualizar(Integer id, ProductoRequest request) {
        Producto producto = buscarOFallar(id);
        Categoria categoria = categoriaService.buscarOFallar(request.categoriaId());

        producto.setCategoria(categoria);
        producto.setNombre(request.nombre());
        producto.setPrecio(request.precio());
        producto.setImagenUrl(request.imagenUrl());

        producto = productoRepository.save(producto);
        return ProductoResponse.fromEntity(producto);
    }

 @Transactional
    public void cambiarEstado(Integer id, Boolean nuevoEstado) {
        Producto producto = buscarOFallar(id);
        producto.setActivo(nuevoEstado);
        productoRepository.save(producto); 
    }

    private Producto buscarOFallar(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un producto con id " + id));
    }
}
