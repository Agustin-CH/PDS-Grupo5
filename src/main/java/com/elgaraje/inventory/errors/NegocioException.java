package com.elgaraje.inventory.errors;

public class NegocioException extends RuntimeException {
  public NegocioException(String mensaje) {
    super(mensaje);
  }
}
