package com.elgaraje.inventory.models;

import jakarta.persistence.*;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CertificadoAutenticidad {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  @JoinColumn(nullable = false, unique = true)
  private Obra obra;

  @Column(nullable = false, unique = true)
  private String numero;

  @Column(nullable = false)
  private LocalDate fechaEmision;

  @Column(nullable = false)
  private String emisor;

  @Column(nullable = false)
  private String firma;
}
