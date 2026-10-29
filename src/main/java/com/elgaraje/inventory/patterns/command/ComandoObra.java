package com.elgaraje.inventory.patterns.command;

public interface ComandoObra {
  String tipo();

  void ejecutar(ContextoOperacion contexto);
}
