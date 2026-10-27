package com.elgaraje.inventory.patterns.decorator;

import java.math.BigDecimal;

public class Enmarcado extends AdicionalDecorator {
  public Enmarcado(CostoVenta base, BigDecimal importe) {
    super(base, importe, "ENMARCADO");
  }
}
