package com.hotelsistema.backend.controller;

import com.hotelsistema.backend.dto.reservaproductoDTO.AgregarProductoRequest;
import com.hotelsistema.backend.dto.reservaproductoDTO.ReservaProductoResponse;
import com.hotelsistema.backend.service.ReservaProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // <-- IMPORTANTE: Evita errores de CORS en el navegador al llamar desde React
@RestController
@RequestMapping("/api/reservas/{reservaId}/productos")
@RequiredArgsConstructor
@Validated // <-- IMPORTANTE: Necesario para que Spring valide la List<AgregarProductoRequest>
@Tag(name = "Consumos de Reserva (Room Service/Kiosko)", description = "Endpoints para gestionar los productos agregados a la cuenta de una reserva específica.")
public class ReservaProductoController {

    private final ReservaProductoService reservaProductoService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'RECEPCIONISTA')") // <-- CORREGIDO: Evita error 403 por prefijos ROLE_
    @Operation(summary = "Agregar múltiples productos a la reserva", 
               description = "Recibe una lista de productos y cantidades para cargarlos al total general de la reserva.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Productos agregados y total actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o la reserva está en un estado no permitido (Finalizada/Anulada)"),
            @ApiResponse(responseCode = "404", description = "Reserva o producto no encontrado")
    })
    public ResponseEntity<List<ReservaProductoResponse>> agregarProductos(
            @Parameter(description = "ID de la reserva", required = true) @PathVariable Integer reservaId,
            @Valid @RequestBody List<AgregarProductoRequest> requests) { 
        return new ResponseEntity<>(reservaProductoService.agregarProductos(reservaId, requests), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los consumos", 
               description = "Devuelve la lista detallada de todos los productos y subtotales cargados actualmente a la habitación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos devuelta con éxito"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<List<ReservaProductoResponse>> obtenerProductos(
            @Parameter(description = "ID de la reserva", required = true) @PathVariable Integer reservaId) {
        return ResponseEntity.ok(reservaProductoService.obtenerProductosDeReserva(reservaId));
    }

    @DeleteMapping("/{reservaProductoId}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'RECEPCIONISTA')") // <-- CORREGIDO: Evita error 403 por prefijos ROLE_
    @Operation(summary = "Eliminar un consumo", 
               description = "Elimina un producto específico de la cuenta de la reserva y recalcula el total general.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado con éxito"),
            @ApiResponse(responseCode = "400", description = "El producto no pertenece a la reserva o la reserva está cerrada"),
            @ApiResponse(responseCode = "404", description = "Registro de producto no encontrado")
    })
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "ID de la reserva", required = true) @PathVariable Integer reservaId,
            @Parameter(description = "ID del registro de consumo a eliminar", required = true) @PathVariable Integer reservaProductoId) {
        reservaProductoService.eliminarProducto(reservaId, reservaProductoId);
        return ResponseEntity.noContent().build();
    }
}