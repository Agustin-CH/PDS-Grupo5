package com.elgaraje.inventory.patterns.decorator;

import java.math.BigDecimal;
import java.util.List;

public record PrecioBase(BigDecimal precio) implements CostoVenta {
  public BigDecimal total() {
    return precio;
  }

  public List<String> servicios() {
    return List.of();
  }
}
