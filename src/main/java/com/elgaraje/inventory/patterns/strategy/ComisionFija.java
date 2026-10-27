package com.elgaraje.inventory.patterns.strategy;

import com.elgaraje.inventory.models.TipoComision;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ComisionFija implements ComisionStrategy {
  public TipoComision tipo() {
    return TipoComision.IMPORTE_FIJO;
  }

  public BigDecimal calcular(BigDecimal precio, BigDecimal valor) {
    return valor.setScale(2, java.math.RoundingMode.HALF_UP);
  }
}
