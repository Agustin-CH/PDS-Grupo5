package com.elgaraje.inventory.patterns.command;

import com.elgaraje.inventory.errors.NegocioException;
import com.elgaraje.inventory.models.EstadoObra;

public record CambiarEstado(EstadoObra destino) implements ComandoObra {
  public String tipo() {
    return "CAMBIO_ESTADO";
  }

  public void ejecutar(ContextoOperacion c) {
    if (destino == EstadoObra.RESERVADA
        || destino == EstadoObra.VENDIDA
        || c.obra().getEstado() == EstadoObra.RESERVADA)
      throw new NegocioException("Utilice las operaciones de reserva, cancelación o venta");
    c.ciclo().cambiar(c.obra(), destino);
  }
}
