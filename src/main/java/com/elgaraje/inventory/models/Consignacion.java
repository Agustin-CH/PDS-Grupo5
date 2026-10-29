package com.elgaraje.inventory.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Consignacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  @JoinColumn(nullable = false, unique = true)
  private Obra obra;

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Artista artista;

  @Column(nullable = false)
  private LocalDate fecha;

  private LocalDate vencimiento;

  @Enumerated(EnumType.STRING)
  private TipoComision tipoComision;

  @Column(precision = 19, scale = 2)
  private BigDecimal valorComision;

  @Version private Long version;
}
