package com.elgaraje.inventory.patterns.command;

import com.elgaraje.inventory.errors.NegocioException;
import com.elgaraje.inventory.models.EstadoObra;

public class VenderObra implements ComandoObra {
  public String tipo() {
    return "VENTA";
  }

  public void ejecutar(ContextoOperacion c) {
    validar(c);
    c.ciclo().cambiar(c.obra(), EstadoObra.VENDIDA);
    c.obra().setComprador(c.comprador());
    c.obra().setReservadoPor(null);
  }

  public void validar(ContextoOperacion c) {
    if (c.obra().getEstado() == EstadoObra.RESERVADA
        && (c.obra().getReservadoPor() == null
            || !c.obra().getReservadoPor().getId().equals(c.comprador().getId())))
      throw new NegocioException("La obra está reservada para otro comprador");
    c.ciclo().validar(c.obra(), EstadoObra.VENDIDA);
  }
}
