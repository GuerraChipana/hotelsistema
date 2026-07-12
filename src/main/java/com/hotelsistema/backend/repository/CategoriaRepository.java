package com.hotelsistema.backend.repository;

import com.hotelsistema.backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByActivoTrue();

    boolean existsByNombreIgnoreCase(String nombre);
}
