package com.elgaraje.inventory.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Artista extends Usuario {

  @Column(columnDefinition = "TEXT")
  private String biografia;

  @com.fasterxml.jackson.annotation.JsonIgnore
  @OneToMany(mappedBy = "artista")
  private List<Obra> obras;

  @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
  private TipoComision tipoComision;

  @Column(precision = 19, scale = 2)
  private java.math.BigDecimal valorComision;
}
