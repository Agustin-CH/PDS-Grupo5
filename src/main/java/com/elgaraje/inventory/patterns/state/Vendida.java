package com.elgaraje.inventory.patterns.state;

import com.elgaraje.inventory.models.EstadoObra;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class Vendida implements EstadoDeObra {
  public EstadoObra codigo() {
    return EstadoObra.VENDIDA;
  }

  public Set<EstadoObra> destinos() {
    return Set.of();
  }
}
