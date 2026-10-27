package com.elgaraje.inventory.patterns.decorator;

import java.math.BigDecimal;
import java.util.List;

public interface CostoVenta {
  BigDecimal total();

  List<String> servicios();
}
