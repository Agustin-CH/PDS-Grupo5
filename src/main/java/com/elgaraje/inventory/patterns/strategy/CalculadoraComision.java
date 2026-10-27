package com.elgaraje.inventory.patterns.strategy;

import com.elgaraje.inventory.errors.NegocioException;
import com.elgaraje.inventory.models.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class CalculadoraComision {
  private final Map<TipoComision, ComisionStrategy> estrategias = new EnumMap<>(TipoComision.class);

  public CalculadoraComision(List<ComisionStrategy> estrategias) {
    estrategias.forEach(e -> this.estrategias.put(e.tipo(), e));
  }

  public BigDecimal calcular(BigDecimal precio, TipoComision tipo, BigDecimal valor) {
    if (precio == null || precio.signum() <= 0)
      throw new NegocioException("La obra requiere un precio positivo");
    validarCondiciones(tipo, valor);
    ComisionStrategy estrategia = estrategias.get(tipo);
    if (estrategia == null) throw new NegocioException("Tipo de comisión no soportado");
    BigDecimal comision = estrategia.calcular(precio, valor);
    if (comision.compareTo(precio) > 0)
      throw new NegocioException("La comisión supera el precio de la obra");
    return comision;
  }

  public void validarCondiciones(TipoComision tipo, BigDecimal valor) {
    if (tipo == null
        || valor == null
        || valor.signum() < 0
        || valor.stripTrailingZeros().scale() > 2
        || valor.precision() - valor.scale() > 17)
      throw new NegocioException("Condiciones de comisión inválidas");
    if (tipo == TipoComision.PORCENTAJE && valor.compareTo(new BigDecimal("100")) > 0)
      throw new NegocioException("El porcentaje debe estar entre 0 y 100");
  }
}
