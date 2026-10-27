package com.elgaraje.inventory.patterns.state;

import com.elgaraje.inventory.models.EstadoObra;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class EnExhibicion implements EstadoDeObra {
  public EstadoObra codigo() {
    return EstadoObra.EN_EXHIBICION;
  }

  public Set<EstadoObra> destinos() {
    return Set.of(
        EstadoObra.DISPONIBLE,
        EstadoObra.RESERVADA,
        EstadoObra.VENDIDA,
        EstadoObra.EN_RESTAURACION);
  }
}
