package com.elgaraje.inventory.errors;

public class NoEncontradoException extends RuntimeException {
  public NoEncontradoException(String recurso) {
    super(recurso + " no encontrado");
  }
}
