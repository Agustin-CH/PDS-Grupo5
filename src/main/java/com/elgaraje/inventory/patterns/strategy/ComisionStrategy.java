package com.elgaraje.inventory.patterns.strategy;

import com.elgaraje.inventory.models.TipoComision;
import java.math.BigDecimal;

public interface ComisionStrategy {
  TipoComision tipo();

  BigDecimal calcular(BigDecimal precio, BigDecimal valor);
}
