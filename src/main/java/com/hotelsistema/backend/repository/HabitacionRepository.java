package com.hotelsistema.backend.repository;

import com.hotelsistema.backend.model.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

    List<Habitacion> findByActivoTrue();

    boolean existsByNumeroHabitacion(String numeroHabitacion);
}
