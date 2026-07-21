package com.hotelsistema.backend.service;

import com.hotelsistema.backend.dto.reservaDTO.CambiarEstadoReservaRequest;
import com.hotelsistema.backend.dto.reservaDTO.CrearReservaRequest;
import com.hotelsistema.backend.dto.reservaDTO.ReservaResponse;
import com.hotelsistema.backend.exception.EstadoReservaInvalidoException;
import com.hotelsistema.backend.exception.HabitacionNoDisponibleException;
import com.hotelsistema.backend.exception.RecursoNoEncontradoException;
import com.hotelsistema.backend.model.EstadoHabitacion;
import com.hotelsistema.backend.model.EstadoReserva;
import com.hotelsistema.backend.model.Habitacion;
import com.hotelsistema.backend.model.ModalidadReserva;
import com.hotelsistema.backend.model.Reserva;
import com.hotelsistema.backend.model.Rol;
import com.hotelsistema.backend.model.Usuario;
import com.hotelsistema.backend.repository.HabitacionRepository;
import com.hotelsistema.backend.repository.ReservaRepository;
import com.hotelsistema.backend.repository.UsuarioRepository;
import com.hotelsistema.backend.security.UserPrincipal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservaService {

    private static final BigDecimal PRECIO_PISCINA_POR_ADULTO = new BigDecimal("15.00");

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HabitacionRepository habitacionRepository;

    // ==========================================
    // 1. MÉTODOS DE CREACIÓN (FLUJOS SEPARADOS)
    // ==========================================

    @Transactional
    public ReservaResponse crearReservaOnline(CrearReservaRequest request, Integer clienteId) {
        // El cliente desde la web siempre genera reservas PENDIENTES
        return procesarReserva(request, clienteId, EstadoReserva.PENDIENTE);
    }

    @Transactional
    public ReservaResponse crearReservaPresencial(CrearReservaRequest request) {
        // El staff en recepción genera reservas PENDIENTES (o PAGADA si pagan inmediatamente)
        return procesarReserva(request, request.usuarioId(), EstadoReserva.PENDIENTE);
    }   

    private ReservaResponse procesarReserva(CrearReservaRequest request, Integer usuarioId, EstadoReserva estadoInicial) {
        if (!request.fechaHoraSalida().isAfter(request.fechaHoraEntrada())) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioId));

        Habitacion habitacion = habitacionRepository.findById(request.habitacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Habitación no encontrada con ID: " + request.habitacionId()));

        if (!Boolean.TRUE.equals(habitacion.getActivo())) {
            throw new HabitacionNoDisponibleException(
                    "La habitacion " + habitacion.getNumeroHabitacion() + " esta fuera de servicio");
        }

        // Validación de solapamientos (Se excluyen ANULADAS y FINALIZADAS)
        List<Reserva> solapadas = reservaRepository.findSolapamientos(
                habitacion.getId(), request.fechaHoraEntrada(), request.fechaHoraSalida(),
                List.of(EstadoReserva.ANULADA, EstadoReserva.FINALIZADA), 
                null);

        if (!solapadas.isEmpty()) {
            throw new HabitacionNoDisponibleException(
                    "La habitacion " + habitacion.getNumeroHabitacion() +
                    " ya tiene una reserva que se cruza con esas fechas");
        }

        int adultos = request.cantAdultos() != null ? request.cantAdultos() : 1;
        int ninos = request.cantNinos() != null ? request.cantNinos() : 0;
        boolean piscina = Boolean.TRUE.equals(request.accesoPiscina());

        BigDecimal costoHabitacion = calcularCostoHabitacion(
                request.modalidad(), habitacion, request.fechaHoraEntrada(), request.fechaHoraSalida());
        
        BigDecimal costoPiscina = piscina
                ? PRECIO_PISCINA_POR_ADULTO.multiply(BigDecimal.valueOf(adultos))
                : BigDecimal.ZERO;
        
        BigDecimal costoProductos = BigDecimal.ZERO; // se va a actualizar cuando agreguemos ReservaProducto
        BigDecimal total = costoHabitacion.add(costoPiscina).add(costoProductos);

        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .habitacion(habitacion)
                .modalidad(request.modalidad())
                .fechaHoraEntrada(request.fechaHoraEntrada())
                .fechaHoraSalida(request.fechaHoraSalida())
                .cantAdultos(adultos)
                .cantNinos(ninos)
                .accesoPiscina(piscina)
                .costoHabitacion(costoHabitacion)
                .costoPiscina(costoPiscina)
                .costoProductos(costoProductos)
                .totalGeneral(total)
                .estado(estadoInicial)
                .build();

        reserva = reservaRepository.save(reserva);
        return ReservaResponse.fromEntity(reserva);
    }

    // ==========================================
    // 2. MÉTODOS DE LECTURA
    // ==========================================

    public List<ReservaResponse> listarMias(Integer usuarioId) {
        return reservaRepository.findByUsuarioIdOrderByFechaHoraEntradaDesc(usuarioId).stream()
                .map(ReservaResponse::fromEntity)
                .toList();
    }

    public List<ReservaResponse> listarTodas() {
        return reservaRepository.findAllByOrderByFechaHoraEntradaDesc().stream()
                .map(ReservaResponse::fromEntity)
                .toList();
    }

    public List<ReservaResponse> listarPorHabitacion(Integer habitacionId) {
        return reservaRepository.findByHabitacionIdOrderByFechaHoraEntradaDesc(habitacionId).stream()
                .map(ReservaResponse::fromEntity)
                .toList();
    }

    public ReservaResponse obtenerPorId(Integer id, UserPrincipal principal) {
        Reserva reserva = buscarOFallar(id);
        validarQuePuedeVerla(reserva, principal);
        return ReservaResponse.fromEntity(reserva);
    }

    // ==========================================
    // 3. MÉTODOS DE ACTUALIZACIÓN
    // ==========================================

    /**
     * Cancelar (anular) una reserva.
     * - El propio CLIENTE dueño solo puede hacerlo si sigue PENDIENTE.
     * - ADMINISTRADOR/RECEPCIONISTA pueden anular cualquier reserva que no
     *   esté ya FINALIZADA o ANULADA.
     */
    @Transactional
    public ReservaResponse cancelar(Integer id, UserPrincipal principal) {
        Reserva reserva = buscarOFallar(id);
        boolean esDueno = reserva.getUsuario().getId().equals(principal.getId());
        boolean esStaff = principal.getUsuario().getRol() != Rol.CLIENTE;

        if (!esDueno && !esStaff) {
            throw new AccessDeniedException("No puedes cancelar la reserva de otro usuario");
        }
        if (reserva.getEstado() == EstadoReserva.FINALIZADA || reserva.getEstado() == EstadoReserva.ANULADA) {
            throw new EstadoReservaInvalidoException(
                    "La reserva ya esta " + reserva.getEstado().name().toLowerCase() + ", no se puede cancelar");
        }
        if (!esStaff && reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new EstadoReservaInvalidoException(
                    "Esta reserva ya fue pagada, no puedes cancelarla tu mismo. Contacta a recepcion.");
        }

        reserva.setEstado(EstadoReserva.ANULADA);
        reserva = reservaRepository.save(reserva);

        // Si se anula una reserva que por algún motivo ya ocupaba la habitación, se libera
        if (reserva.getHabitacion().getEstado() == EstadoHabitacion.OCUPADO) {
            reserva.getHabitacion().setEstado(EstadoHabitacion.LIBRE);
            habitacionRepository.save(reserva.getHabitacion());
        }

        return ReservaResponse.fromEntity(reserva);
    }

    /**
     * Transiciones operativas (PENDIENTE -> PAGADA -> FINALIZADA), solo staff.
     */
    @Transactional
    public ReservaResponse cambiarEstado(Integer id, CambiarEstadoReservaRequest request) {
        Reserva reserva = buscarOFallar(id);
        
        // Validaciones previas
        if (reserva.getEstado() == EstadoReserva.FINALIZADA || reserva.getEstado() == EstadoReserva.ANULADA) {
            throw new EstadoReservaInvalidoException(
                    "No se puede modificar una reserva que ya esta " + reserva.getEstado().name().toLowerCase());
        }

        reserva.setEstado(request.estado());
        reserva = reservaRepository.save(reserva);

        // NUEVA LÓGICA: Sincronizar la habitación físicamente
        Habitacion habitacion = reserva.getHabitacion();
        
        if (request.estado() == EstadoReserva.PAGADA) { 
            // Check-In: El cliente llega, paga y entra a la habitación
            habitacion.setEstado(EstadoHabitacion.OCUPADO);
        } else if (request.estado() == EstadoReserva.FINALIZADA) { 
            // Check-Out: El cliente se retira, la habitación pasa a mantenimiento/limpieza
            habitacion.setEstado(EstadoHabitacion.MANTENIMIENTO); 
        } else if (request.estado() == EstadoReserva.ANULADA && habitacion.getEstado() == EstadoHabitacion.OCUPADO) {
            // Liberación de emergencia
            habitacion.setEstado(EstadoHabitacion.LIBRE);
        }
        
        habitacionRepository.save(habitacion);

        return ReservaResponse.fromEntity(reserva);
    }

    // ==========================================
    // 4. MÉTODOS PRIVADOS AUXILIARES
    // ==========================================

    private void validarQuePuedeVerla(Reserva reserva, UserPrincipal principal) {
        boolean esDueno = reserva.getUsuario().getId().equals(principal.getId());
        boolean esStaff = principal.getUsuario().getRol() != Rol.CLIENTE;
        if (!esDueno && !esStaff) {
            throw new AccessDeniedException("No puedes ver la reserva de otro usuario");
        }
    }

    /**
     * Calcula el costo de la habitacion SIEMPRE en el servidor, redondeando
     * hacia arriba (una reserva de 1 dia y 2 horas se cobra como 2 dias).
     */
    private BigDecimal calcularCostoHabitacion(ModalidadReserva modalidad, Habitacion habitacion,
                                               LocalDateTime entrada, LocalDateTime salida) {
        long totalMinutos = Duration.between(entrada, salida).toMinutes();

        if (modalidad == ModalidadReserva.POR_DIA) {
            long dias = Math.max(1, (long) Math.ceil(totalMinutos / (60.0 * 24)));
            return habitacion.getPrecioDia().multiply(BigDecimal.valueOf(dias));
        } else {
            long horas = Math.max(1, (long) Math.ceil(totalMinutos / 60.0));
            return habitacion.getPrecioHora().multiply(BigDecimal.valueOf(horas));
        }
    }

    private Reserva buscarOFallar(Integer id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una reserva con id " + id));
    }
}