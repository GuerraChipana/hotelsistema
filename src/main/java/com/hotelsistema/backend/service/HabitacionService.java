package com.hotelsistema.backend.service;

import com.hotelsistema.backend.dto.habitacionesDTO.ActualizarHabitacionRequest;
import com.hotelsistema.backend.dto.habitacionesDTO.CambiarEstadoHabitacionRequest;
import com.hotelsistema.backend.dto.habitacionesDTO.CrearHabitacionRequest;
import com.hotelsistema.backend.dto.habitacionesDTO.HabitacionResponse;
import com.hotelsistema.backend.exception.RecursoDuplicadoException;
import com.hotelsistema.backend.exception.RecursoNoEncontradoException;
import com.hotelsistema.backend.model.EstadoHabitacion;
import com.hotelsistema.backend.model.Habitacion;
import com.hotelsistema.backend.repository.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public HabitacionResponse crear(CrearHabitacionRequest request) {
        if (habitacionRepository.existsByNumeroHabitacion(request.numeroHabitacion())) {
            throw new RecursoDuplicadoException(
                    "Ya existe una habitacion con el numero " + request.numeroHabitacion());
        }

        Habitacion habitacion = Habitacion.builder()
                .numeroHabitacion(request.numeroHabitacion())
                .tipo(request.tipo())
                .precioDia(request.precioDia())
                .precioHora(request.precioHora())
                .imagenUrl(request.imagenUrl()) // <--- AGREGAR ESTA LÍNEA
                .estado(EstadoHabitacion.LIBRE)
                .activo(true)
                .build();

        habitacion = habitacionRepository.save(habitacion);
        return HabitacionResponse.fromEntity(habitacion);
    }

    public List<HabitacionResponse> listarActivas() {
        return habitacionRepository.findByActivoTrue().stream()
                .map(HabitacionResponse::fromEntity)
                .toList();
    }

    public List<HabitacionResponse> listarTodas() {
        return habitacionRepository.findAll().stream()
                .map(HabitacionResponse::fromEntity)
                .toList();
    }

    public HabitacionResponse obtenerPorId(Integer id) {
        return HabitacionResponse.fromEntity(buscarOFallar(id));
    }

    public HabitacionResponse actualizar(Integer id, ActualizarHabitacionRequest request) {
        Habitacion habitacion = buscarOFallar(id);
        habitacion.setTipo(request.tipo());
        habitacion.setPrecioDia(request.precioDia());
        habitacion.setPrecioHora(request.precioHora());
        habitacion.setImagenUrl(request.imagenUrl());
        habitacion = habitacionRepository.save(habitacion);
        return HabitacionResponse.fromEntity(habitacion);
    }

    public HabitacionResponse cambiarEstado(Integer id, CambiarEstadoHabitacionRequest request) {
        Habitacion habitacion = buscarOFallar(id);
        habitacion.setEstado(request.estado());
        habitacion = habitacionRepository.save(habitacion);
        return HabitacionResponse.fromEntity(habitacion);
    }

// Ahora recibe el ID y el nuevo estado
    public void cambiarEstado(Integer id, Boolean nuevoEstado) {
        Habitacion habitacion = buscarOFallar(id);
        habitacion.setActivo(nuevoEstado);
        habitacionRepository.save(habitacion);
    }
    public Habitacion buscarOFallar(Integer id) {
        return habitacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una habitacion con id " + id));
    }
}
