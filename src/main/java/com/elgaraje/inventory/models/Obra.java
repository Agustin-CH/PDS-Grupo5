package com.elgaraje.inventory.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Obra {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String titulo;

  private Integer anio;

  private String dimensiones;

  @Column(columnDefinition = "TEXT")
  private String descripcion;

  @Enumerated(EnumType.STRING)
  private EstadoObra estado = EstadoObra.DISPONIBLE;

  @Version private Long version;

  @Column(precision = 19, scale = 2)
  private java.math.BigDecimal precio;

  @ManyToOne private Comprador reservadoPor;

  @ManyToOne private Ubicacion ubicacion;

  @ElementCollection private java.util.List<String> fotos = new java.util.ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "artista_id")
  private Artista artista;

  @ManyToOne
  @JoinColumn(name = "comprador_id")
  private Comprador comprador;
}
