package com.elgaraje.inventory.models;

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
public class Comprador extends Usuario {

  @com.fasterxml.jackson.annotation.JsonIgnore
  @OneToMany(mappedBy = "comprador")
  private List<Obra> obrasAdquiridas;
}
