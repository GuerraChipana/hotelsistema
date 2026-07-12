package com.hotelsistema.backend.service;

import com.hotelsistema.backend.dto.reservaproductoDTO.AgregarProductoRequest;
import com.hotelsistema.backend.dto.reservaproductoDTO.ReservaProductoResponse;
import com.hotelsistema.backend.exception.EstadoReservaInvalidoException;
import com.hotelsistema.backend.exception.RecursoNoEncontradoException;
import com.hotelsistema.backend.model.Producto;
import com.hotelsistema.backend.model.Reserva;
import com.hotelsistema.backend.model.ReservaProducto;
import com.hotelsistema.backend.repository.ProductoRepository;
import com.hotelsistema.backend.repository.ReservaProductoRepository;
import com.hotelsistema.backend.repository.ReservaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaProductoService {

    private final ReservaProductoRepository reservaProductoRepository;
    private final ReservaRepository reservaRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public ReservaProductoResponse agregarProducto(Integer reservaId, AgregarProductoRequest request) { // <-- CORREGIDO
        Reserva reserva = buscarReserva(reservaId);
        validarEstadoReserva(reserva);

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + request.getProductoId()));

        BigDecimal precioHistorico = producto.getPrecio();
        BigDecimal subtotal = precioHistorico.multiply(BigDecimal.valueOf(request.getCantidad()));

        ReservaProducto reservaProducto = ReservaProducto.builder()
                .reserva(reserva)
                .producto(producto)
                .cantidad(request.getCantidad())
                .precioUnitario(precioHistorico)
                .subtotal(subtotal)
                .build();

        ReservaProducto guardado = reservaProductoRepository.save(reservaProducto);
        actualizarTotalesReserva(reserva);

        return mapearAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<ReservaProductoResponse> obtenerProductosDeReserva(Integer reservaId) { // <-- CORREGIDO
        if (!reservaRepository.existsById(reservaId)) {
            throw new RecursoNoEncontradoException("Reserva no encontrada con ID: " + reservaId);
        }
        return reservaProductoRepository.findByReservaId(reservaId).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarProducto(Integer reservaId, Integer reservaProductoId) { // <-- CORREGIDO
        Reserva reserva = buscarReserva(reservaId);
        validarEstadoReserva(reserva);

        ReservaProducto reservaProducto = reservaProductoRepository.findById(reservaProductoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de producto no encontrado"));

        if (!reservaProducto.getReserva().getId().equals(reservaId)) {
            throw new IllegalArgumentException("Este producto no pertenece a la reserva indicada");
        }

        reservaProductoRepository.delete(reservaProducto);
        reservaProductoRepository.flush();
        actualizarTotalesReserva(reserva);
    }

    private Reserva buscarReserva(Integer reservaId) { // <-- CORREGIDO
        return reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + reservaId));
    }

    private void validarEstadoReserva(Reserva reserva) {
        String estado = reserva.getEstado().name();
        if (estado.equals("ANULADA") || estado.equals("FINALIZADA")) {
            throw new EstadoReservaInvalidoException("No se pueden modificar productos en una reserva " + estado);
        }
    }

    private void actualizarTotalesReserva(Reserva reserva) {
        BigDecimal totalProductos = reservaProductoRepository.findByReservaId(reserva.getId()).stream()
                .map(ReservaProducto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        reserva.setCostoProductos(totalProductos);
        reserva.setTotalGeneral(reserva.getCostoHabitacion().add(totalProductos));
        reservaRepository.save(reserva);
    }

    private ReservaProductoResponse mapearAResponse(ReservaProducto rp) {
        return ReservaProductoResponse.builder()
                .id(rp.getId())
                .reservaId(rp.getReserva().getId())
                .productoId(rp.getProducto().getId())
                .nombreProducto(rp.getProducto().getNombre())
                .cantidad(rp.getCantidad())
                .precioUnitario(rp.getPrecioUnitario())
                .subtotal(rp.getSubtotal())
                .build();
    }
}