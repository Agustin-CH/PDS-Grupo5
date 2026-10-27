package com.elgaraje.inventory.patterns.decorator;

import java.math.BigDecimal;

public class Envio extends AdicionalDecorator {
  public Envio(CostoVenta base, BigDecimal importe) {
    super(base, importe, "ENVIO");
  }
}
