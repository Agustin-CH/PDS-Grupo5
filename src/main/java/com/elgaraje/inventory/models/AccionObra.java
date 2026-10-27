package com.elgaraje.inventory.models;

import jakarta.persistence.*;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AccionObra {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Obra obra;

  @Column(nullable = false)
  private String tipo;

  @Column(nullable = false)
  private Instant fecha;

  @Enumerated(EnumType.STRING)
  private EstadoObra estadoAnterior;

  @Enumerated(EnumType.STRING)
  private EstadoObra estadoNuevo;

  private Long ubicacionAnteriorId;
  private Long ubicacionNuevaId;

  @Column(length = 2000)
  private String detalle;
}
