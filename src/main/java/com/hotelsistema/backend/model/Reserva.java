package com.hotelsistema.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // A diferencia de Producto.categoria (LAZY), aqui usamos el default EAGER a
    // proposito: usuario y habitacion se necesitan siempre que se arma una
    // ReservaResponse, y evita tener que acordarse de poner @Transactional
    // en cada metodo de lectura del servicio (open-in-view esta en false).
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    @Column(name = "modalidad", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModalidadReserva modalidad;

    @Column(name = "fecha_hora_entrada", nullable = false)
    private LocalDateTime fechaHoraEntrada;

    @Column(name = "fecha_hora_salida", nullable = false)
    private LocalDateTime fechaHoraSalida;

    @Column(name = "cant_adultos")
    private Integer cantAdultos;

    @Column(name = "cant_ninos")
    private Integer cantNinos;

    @Column(name = "acceso_piscina")
    private Boolean accesoPiscina;

    @Column(name = "costo_habitacion", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoHabitacion;

    @Column(name = "costo_piscina", precision = 10, scale = 2)
    private BigDecimal costoPiscina;

    @Column(name = "costo_productos", precision = 10, scale = 2)
    private BigDecimal costoProductos;

    @Column(name = "total_general", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalGeneral;

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    @Column(name = "fecha_creacion", updatable = false, insertable = false)
    private LocalDateTime fechaCreacion;
}
