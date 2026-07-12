package com.hotelsistema.backend.repository;

import com.hotelsistema.backend.model.ReservaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservaProductoRepository extends JpaRepository<ReservaProducto, Integer> {
    List<ReservaProducto> findByReservaId(Integer reservaId);
}