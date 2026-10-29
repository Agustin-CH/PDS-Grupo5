package com.elgaraje.inventory.patterns.factory;

import com.elgaraje.inventory.models.*;
import com.elgaraje.inventory.patterns.decorator.CostoVenta;
import com.elgaraje.inventory.patterns.strategy.CalculadoraComision;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;

public abstract class CreadorVenta {
  private final CalculadoraComision calculadora;

  protected CreadorVenta(CalculadoraComision calculadora) {
    this.calculadora = calculadora;
  }

  // El algoritmo común delega la creación del producto en el Factory Method.
  public Venta preparar(
      Consignacion consignacion, Comprador comprador, CostoVenta costo, Instant fecha) {
    Venta venta = crearVenta(consignacion, comprador);
    Artista artista = consignacion.getArtista();
    TipoComision tipo =
        consignacion.getTipoComision() != null
            ? consignacion.getTipoComision()
            : artista.getTipoComision();
    BigDecimal valor =
        consignacion.getValorComision() != null
            ? consignacion.getValorComision()
            : artista.getValorComision();
    BigDecimal precio = consignacion.getObra().getPrecio();
    BigDecimal comision = calculadora.calcular(precio, tipo, valor);
    venta.setFecha(fecha);
    venta.setPrecioObra(precio);
    venta.setMonto(costo.total());
    venta.setAdicionales(costo.total().subtract(precio));
    venta.setComisionGaleria(comision);
    venta.setLiquidacionArtista(precio.subtract(comision));
    venta.setTipoComisionAplicada(tipo);
    venta.setValorComisionAplicado(valor);
    venta.setServicios(new ArrayList<>(costo.servicios()));
    return venta;
  }

  protected abstract Venta crearVenta(Consignacion consignacion, Comprador comprador);
}
