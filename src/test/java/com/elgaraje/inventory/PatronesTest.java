package com.elgaraje.inventory;

import static org.assertj.core.api.Assertions.*;

import com.elgaraje.inventory.errors.NegocioException;
import com.elgaraje.inventory.models.*;
import com.elgaraje.inventory.patterns.command.*;
import com.elgaraje.inventory.patterns.decorator.*;
import com.elgaraje.inventory.patterns.factory.*;
import com.elgaraje.inventory.patterns.state.*;
import com.elgaraje.inventory.patterns.strategy.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PatronesTest {
  private final CicloObra ciclo =
      new CicloObra(
          List.of(
              new Disponible(),
              new EnExhibicion(),
              new Reservada(),
              new Vendida(),
              new EnRestauracion()));
  private final CalculadoraComision calculadora =
      new CalculadoraComision(List.of(new ComisionPorcentaje(), new ComisionFija()));

  @ParameterizedTest
  @CsvSource({
    "DISPONIBLE,RESERVADA",
    "RESERVADA,VENDIDA",
    "EN_EXHIBICION,VENDIDA",
    "EN_RESTAURACION,DISPONIBLE"
  })
  void permiteTransiciones(EstadoObra origen, EstadoObra destino) {
    Obra o = new Obra();
    o.setEstado(origen);
    ciclo.cambiar(o, destino);
    assertThat(o.getEstado()).isEqualTo(destino);
  }

  @ParameterizedTest
  @CsvSource({
    "VENDIDA,DISPONIBLE",
    "EN_RESTAURACION,VENDIDA",
    "RESERVADA,EN_EXHIBICION",
    "DISPONIBLE,DISPONIBLE"
  })
  void rechazaTransiciones(EstadoObra origen, EstadoObra destino) {
    Obra o = new Obra();
    o.setEstado(origen);
    assertThatThrownBy(() -> ciclo.cambiar(o, destino)).isInstanceOf(NegocioException.class);
    assertThat(o.getEstado()).isEqualTo(origen);
  }

  @Test
  void componeAdicionalesYFactoryConservaLaLiquidacion() {
    Artista a = new Artista();
    a.setTipoComision(TipoComision.PORCENTAJE);
    a.setValorComision(new BigDecimal("10"));
    Obra o = new Obra();
    o.setPrecio(new BigDecimal("1000"));
    Consignacion c = new Consignacion();
    c.setArtista(a);
    c.setObra(o);
    CostoVenta costo =
        new Seguro(
            new Envio(
                new Enmarcado(new PrecioBase(o.getPrecio()), new BigDecimal("50")),
                new BigDecimal("30")),
            new BigDecimal("20"));
    Venta v =
        new CreadorVentaConsignada(calculadora).preparar(c, new Comprador(), costo, Instant.EPOCH);
    assertThat(v.getMonto()).isEqualByComparingTo("1100");
    assertThat(v.getComisionGaleria()).isEqualByComparingTo("100");
    assertThat(v.getLiquidacionArtista()).isEqualByComparingTo("900");
    assertThat(v.getServicios()).hasSize(3);
    assertThat(v.getConsignacion()).isSameAs(c);
    c.setTipoComision(TipoComision.IMPORTE_FIJO);
    c.setValorComision(new BigDecimal("70"));
    assertThat(
            new CreadorVentaConsignada(calculadora)
                .preparar(c, new Comprador(), costo, Instant.EPOCH)
                .getComisionGaleria())
        .isEqualByComparingTo("70");
  }

  @Test
  void redondeaYRechazaCondicionesInvalidas() {
    assertThat(
            calculadora.calcular(
                new BigDecimal("10.05"), TipoComision.PORCENTAJE, new BigDecimal("10")))
        .isEqualByComparingTo("1.01");
    assertThatThrownBy(
            () ->
                calculadora.calcular(
                    new BigDecimal("10"), TipoComision.PORCENTAJE, new BigDecimal("101")))
        .isInstanceOf(NegocioException.class);
    assertThatThrownBy(
            () ->
                calculadora.calcular(
                    new BigDecimal("10"), TipoComision.IMPORTE_FIJO, new BigDecimal("11")))
        .isInstanceOf(NegocioException.class);
  }

  @Test
  void comandoNoVendeReservaAjena() {
    Comprador c = new Comprador();
    c.setId(1L);
    Comprador otro = new Comprador();
    otro.setId(2L);
    Obra o = new Obra();
    new ReservarObra().ejecutar(new ContextoOperacion(o, c, null, ciclo));
    assertThatThrownBy(() -> new VenderObra().ejecutar(new ContextoOperacion(o, otro, null, ciclo)))
        .isInstanceOf(NegocioException.class);
    new CancelarReserva().ejecutar(new ContextoOperacion(o, null, null, ciclo));
    assertThat(o.getEstado()).isEqualTo(EstadoObra.DISPONIBLE);
    assertThat(o.getReservadoPor()).isNull();
  }

  @Test
  void rechazaEstadosNulosYPrecisionInvalida() {
    Obra o = new Obra();
    assertThatThrownBy(() -> ciclo.cambiar(o, null)).isInstanceOf(NegocioException.class);
    o.setEstado(null);
    assertThatThrownBy(() -> ciclo.cambiar(o, EstadoObra.DISPONIBLE))
        .isInstanceOf(NegocioException.class);
    assertThatThrownBy(
            () ->
                calculadora.validarCondiciones(TipoComision.IMPORTE_FIJO, new BigDecimal("1.001")))
        .isInstanceOf(NegocioException.class);
    assertThatThrownBy(
            () ->
                calculadora.validarCondiciones(
                    TipoComision.IMPORTE_FIJO, new BigDecimal("100000000000000000")))
        .isInstanceOf(NegocioException.class);
    CalculadoraComision sinEstrategias = new CalculadoraComision(List.of());
    assertThatThrownBy(
            () -> sinEstrategias.calcular(BigDecimal.TEN, TipoComision.PORCENTAJE, BigDecimal.ONE))
        .isInstanceOf(NegocioException.class);
  }
}
