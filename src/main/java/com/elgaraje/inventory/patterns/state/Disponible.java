package com.elgaraje.inventory.patterns.state;

import com.elgaraje.inventory.models.EstadoObra;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class Disponible implements EstadoDeObra {
  public EstadoObra codigo() {
    return EstadoObra.DISPONIBLE;
  }

  public Set<EstadoObra> destinos() {
    return Set.of(
        EstadoObra.RESERVADA,
        EstadoObra.VENDIDA,
        EstadoObra.EN_EXHIBICION,
        EstadoObra.EN_RESTAURACION);
  }
}
