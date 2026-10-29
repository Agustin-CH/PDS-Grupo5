package com.elgaraje.inventory.patterns.factory;

import com.elgaraje.inventory.models.*;
import com.elgaraje.inventory.patterns.strategy.CalculadoraComision;
import org.springframework.stereotype.Component;

@Component
public class CreadorVentaConsignada extends CreadorVenta {
  public CreadorVentaConsignada(CalculadoraComision calculadora) {
    super(calculadora);
  }

  @Override
  protected Venta crearVenta(Consignacion consignacion, Comprador comprador) {
    Venta venta = new Venta();
    venta.setConsignacion(consignacion);
    venta.setObra(consignacion.getObra());
    venta.setComprador(comprador);
    return venta;
  }
}
