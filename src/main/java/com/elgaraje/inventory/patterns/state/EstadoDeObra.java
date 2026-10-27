package com.elgaraje.inventory.patterns.state;

import com.elgaraje.inventory.errors.NegocioException;
import com.elgaraje.inventory.models.EstadoObra;
import java.util.Set;

public interface EstadoDeObra {
  EstadoObra codigo();

  Set<EstadoObra> destinos();

  default void validar(EstadoObra destino) {
    if (!destinos().contains(destino))
      throw new NegocioException("Transición inválida: " + codigo() + " -> " + destino);
  }
}
