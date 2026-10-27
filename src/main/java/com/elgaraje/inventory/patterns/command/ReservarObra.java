package com.elgaraje.inventory.patterns.command;

import com.elgaraje.inventory.models.EstadoObra;

public class ReservarObra implements ComandoObra {
  public String tipo() {
    return "RESERVA";
  }

  public void ejecutar(ContextoOperacion c) {
    c.ciclo().cambiar(c.obra(), EstadoObra.RESERVADA);
    c.obra().setReservadoPor(c.comprador());
  }
}
