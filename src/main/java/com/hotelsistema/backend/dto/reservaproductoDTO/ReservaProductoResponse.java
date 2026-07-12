package com.hotelsistema.backend.dto.reservaproductoDTO;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ReservaProductoResponse {
    private Integer id;
    private Integer reservaId;
    private Integer productoId;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}