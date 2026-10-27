package com.elgaraje.inventory.models;

import jakarta.persistence.*;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Notificacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Obra obra;

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Usuario destinatario;

  @Column(nullable = false)
  private String evento;

  @Column(nullable = false, length = 2000)
  private String mensaje;

  @Column(nullable = false)
  private Instant fecha;
}
