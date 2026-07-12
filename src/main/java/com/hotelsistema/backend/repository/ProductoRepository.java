package com.hotelsistema.backend.repository;

import com.hotelsistema.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByActivoTrue();

    List<Producto> findByActivoTrueAndCategoriaId(Integer categoriaId);
}
