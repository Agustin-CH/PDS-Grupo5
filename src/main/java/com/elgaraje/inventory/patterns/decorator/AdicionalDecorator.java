package com.elgaraje.inventory.patterns.decorator;

import com.elgaraje.inventory.errors.NegocioException;
import java.math.BigDecimal;
import java.util.*;

public abstract class AdicionalDecorator implements CostoVenta {
  private final CostoVenta base;
  private final BigDecimal importe;
  private final String nombre;

  protected AdicionalDecorator(CostoVenta base, BigDecimal importe, String nombre) {
    if (importe == null || importe.signum() < 0 || importe.scale() > 2)
      throw new NegocioException("Importe del adicional inválido");
    this.base = base;
    this.importe = importe;
    this.nombre = nombre;
  }

  public BigDecimal total() {
    return base.total().add(importe);
  }

  public List<String> servicios() {
    List<String> lista = new ArrayList<>(base.servicios());
    lista.add(nombre + ": " + importe.toPlainString());
    return List.copyOf(lista);
  }
}
