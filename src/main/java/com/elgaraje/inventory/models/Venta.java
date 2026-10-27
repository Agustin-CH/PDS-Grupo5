package com.elgaraje.inventory.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Venta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  @JoinColumn(nullable = false, unique = true)
  private Obra obra;

  @OneToOne(optional = false)
  @JoinColumn(nullable = false, unique = true)
  private Consignacion consignacion;

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Comprador comprador;

  @Column(nullable = false)
  private Instant fecha;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal precioObra;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal adicionales;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal monto;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal comisionGaleria;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal liquidacionArtista;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoComision tipoComisionAplicada;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal valorComisionAplicado;

  @ElementCollection private java.util.List<String> servicios = new java.util.ArrayList<>();
}
