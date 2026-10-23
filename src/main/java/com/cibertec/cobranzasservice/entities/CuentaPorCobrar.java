package com.cibertec.cobranzasservice.entities;

import com.cibertec.cobranzasservice.entities.enums.EstadoCuenta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "cuentas_por_cobrar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"servicio"})
public class CuentaPorCobrar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @Column(name = "socio_id")
    private Long socioId;

    @Column(name = "puesto_id")
    private Long puestoId;

    @Column(nullable = false, length = 20)
    private String periodo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private EstadoCuenta estado = EstadoCuenta.PENDIENTE;

    @Column(name = "lectura_inicial", precision = 12, scale = 2)
    private BigDecimal lecturaInicial;

    @Column(name = "lectura_final", precision = 12, scale = 2)
    private BigDecimal lecturaFinal;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDate fechaGeneracion;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;
}
