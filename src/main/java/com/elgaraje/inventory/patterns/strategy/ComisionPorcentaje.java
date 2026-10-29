package com.elgaraje.inventory.patterns.strategy;

import com.elgaraje.inventory.models.TipoComision;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ComisionPorcentaje implements ComisionStrategy {
  public TipoComision tipo() {
    return TipoComision.PORCENTAJE;
  }

  public BigDecimal calcular(BigDecimal precio, BigDecimal valor) {
    return precio.multiply(valor).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
  }
}
