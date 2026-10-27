package com.elgaraje.inventory.patterns.command;

import com.elgaraje.inventory.errors.NegocioException;
import com.elgaraje.inventory.models.EstadoObra;

public class CancelarReserva implements ComandoObra {
  public String tipo() {
    return "CANCELACION_RESERVA";
  }

  public void ejecutar(ContextoOperacion c) {
    if (c.obra().getEstado() != EstadoObra.RESERVADA)
      throw new NegocioException("La obra no está reservada");
    c.ciclo().cambiar(c.obra(), EstadoObra.DISPONIBLE);
    c.obra().setReservadoPor(null);
  }
}
