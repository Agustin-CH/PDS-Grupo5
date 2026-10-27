package com.elgaraje.inventory;

import static org.assertj.core.api.Assertions.*;

import com.elgaraje.inventory.models.*;
import com.elgaraje.inventory.repositories.*;
import java.math.BigDecimal;
import java.time.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(showSql = false)
@ActiveProfiles("test")
class PersistenciaTest {
  @Autowired ArtistaRepository artistas;
  @Autowired CompradorRepository compradores;
  @Autowired ObraRepository obras;
  @Autowired ConsignacionRepository consignaciones;
  @Autowired VentaRepository ventas;

  @Test
  void conservaRelacionesYRechazaUnaSegundaVentaDeLaObra() {
    Artista artista = new Artista();
    artista.setDni("1");
    artista.setNombre("Ana");
    artistas.saveAndFlush(artista);
    Comprador comprador = new Comprador();
    comprador.setDni("2");
    compradores.saveAndFlush(comprador);
    Obra obra = new Obra();
    obra.setTitulo("Luz");
    obra.setArtista(artista);
    obras.saveAndFlush(obra);
    Consignacion consignacion = new Consignacion();
    consignacion.setObra(obra);
    consignacion.setArtista(artista);
    consignacion.setFecha(LocalDate.now());
    consignaciones.saveAndFlush(consignacion);
    Venta venta = venta(obra, comprador, consignacion);
    ventas.saveAndFlush(venta);
    assertThat(ventas.findByObraId(obra.getId())).isPresent();
    assertThatThrownBy(() -> ventas.saveAndFlush(venta(obra, comprador, consignacion)))
        .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
  }

  private Venta venta(Obra obra, Comprador comprador, Consignacion consignacion) {
    Venta v = new Venta();
    v.setObra(obra);
    v.setComprador(comprador);
    v.setConsignacion(consignacion);
    v.setFecha(Instant.now());
    v.setPrecioObra(new BigDecimal("100"));
    v.setAdicionales(BigDecimal.ZERO);
    v.setMonto(new BigDecimal("100"));
    v.setComisionGaleria(BigDecimal.ZERO);
    v.setLiquidacionArtista(new BigDecimal("100"));
    v.setTipoComisionAplicada(TipoComision.PORCENTAJE);
    v.setValorComisionAplicado(BigDecimal.ZERO);
    return v;
  }
}
