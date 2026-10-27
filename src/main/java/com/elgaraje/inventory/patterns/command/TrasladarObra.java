package com.elgaraje.inventory.patterns.command;

import com.elgaraje.inventory.errors.NegocioException;
import com.elgaraje.inventory.models.EstadoObra;

public class TrasladarObra implements ComandoObra {
  public String tipo() {
    return "TRASLADO";
  }

  public void ejecutar(ContextoOperacion c) {
    if (c.obra().getEstado() == EstadoObra.VENDIDA)
      throw new NegocioException("No se puede trasladar una obra vendida");
    if (c.obra().getUbicacion() != null
        && c.obra().getUbicacion().getId().equals(c.ubicacion().getId()))
      throw new NegocioException("La obra ya se encuentra en esa ubicación");
    c.obra().setUbicacion(c.ubicacion());
  }
}
