package com.elgaraje.inventory.patterns.decorator;

import java.math.BigDecimal;

public class Seguro extends AdicionalDecorator {
  public Seguro(CostoVenta base, BigDecimal importe) {
    super(base, importe, "SEGURO");
  }
}
