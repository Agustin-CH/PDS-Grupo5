package com.elgaraje.inventory;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.elgaraje.inventory.dtos.Respuestas.*;
import com.elgaraje.inventory.dtos.Solicitudes.*;
import com.elgaraje.inventory.errors.NegocioException;
import com.elgaraje.inventory.models.*;
import com.elgaraje.inventory.patterns.observer.NotificadorObra;
import com.elgaraje.inventory.repositories.*;
import com.elgaraje.inventory.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CircuitoRestTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;
  @Autowired CatalogoServicio catalogo;
  @Autowired OperacionesServicio operaciones;
  @Autowired VentaRepository ventas;
  @SpyBean NotificadorObra observador;
  private Persona artista, comprador, otro;
  private Pieza obra;
  private Lugar destino;

  @BeforeEach
  void preparar() {
    String dni = UUID.randomUUID().toString();
    artista =
        catalogo.crearArtista(
            new ArtistaAlta(dni, "Ana", "Luz", "", TipoComision.PORCENTAJE, new BigDecimal("10")));
    comprador = catalogo.crearComprador(new CompradorAlta(dni + "c", "Juan", "Sol"));
    otro = catalogo.crearComprador(new CompradorAlta(dni + "d", "Eva", "Mar"));
    Lugar origen = catalogo.crearUbicacion(new UbicacionAlta("Depósito", null));
    destino = catalogo.crearUbicacion(new UbicacionAlta("Sala", null));
    obra =
        catalogo.crearObra(
            new ObraAlta(
                "Luz",
                2026,
                "20x30",
                "Óleo",
                artista.id(),
                origen.id(),
                new BigDecimal("1000"),
                List.of("foto.jpg")));
    catalogo.crearConsignacion(
        new ConsignacionAlta(obra.id(), LocalDate.now().minusDays(1), null, null, null));
  }

  private VentaAlta pedido() {
    return new VentaAlta(
        comprador.id(),
        List.of(
            new Adicional(TipoAdicional.ENMARCADO, new BigDecimal("50")),
            new Adicional(TipoAdicional.ENVIO, new BigDecimal("30")),
            new Adicional(TipoAdicional.SEGURO, new BigDecimal("20"))));
  }

  private String url(String sufijo) {
    return "/api/obras/" + obra.id() + sufijo;
  }

  @Test
  void circuitoCompletoConHistorialNotificacionesYCertificado() throws Exception {
    mvc.perform(
            post("/api/certificados")
                .contentType("application/json")
                .content(
                    json.writeValueAsString(
                        new CertificadoAlta(
                            obra.id(),
                            UUID.randomUUID().toString(),
                            LocalDate.now(),
                            "Ana",
                            "firma de ejemplo"))))
        .andExpect(status().isCreated());
    mvc.perform(
            post(url("/traslados"))
                .contentType("application/json")
                .content(json.writeValueAsString(new Traslado(destino.id()))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ubicacionId").value(destino.id()));
    mvc.perform(
            post(url("/reservas"))
                .contentType("application/json")
                .content(json.writeValueAsString(new Reserva(comprador.id()))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estado").value("RESERVADA"));
    mvc.perform(
            post(url("/cotizacion"))
                .contentType("application/json")
                .content(json.writeValueAsString(pedido())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.monto").value(1100));
    assertThat(operaciones.historial(obra.id())).hasSize(2);
    mvc.perform(
            post(url("/ventas"))
                .contentType("application/json")
                .content(json.writeValueAsString(pedido())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.comisionGaleria").value(100))
        .andExpect(jsonPath("$.liquidacionArtista").value(900))
        .andExpect(jsonPath("$.servicios.length()").value(3));
    mvc.perform(get(url("")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estado").value("VENDIDA"))
        .andExpect(jsonPath("$.compradorId").value(comprador.id()))
        .andExpect(jsonPath("$.reservadoPorId").isEmpty());
    mvc.perform(get(url("/historial")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3));
    mvc.perform(get(url("/certificado")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.obraId").value(obra.id()));
    mvc.perform(get("/api/usuarios/" + comprador.id() + "/notificaciones"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[1].evento").value("VENTA"));
    assertThat(catalogo.notificaciones(artista.id())).hasSize(3);
    mvc.perform(
            post(url("/ventas"))
                .contentType("application/json")
                .content(json.writeValueAsString(pedido())))
        .andExpect(status().isConflict());
    assertThat(operaciones.historial(obra.id())).hasSize(3);
  }

  @Test
  void reservaAjenaCancelacionYEstadosInvalidos() throws Exception {
    operaciones.reservar(obra.id(), new Reserva(otro.id()));
    mvc.perform(
            post(url("/ventas"))
                .contentType("application/json")
                .content(json.writeValueAsString(pedido())))
        .andExpect(status().isConflict());
    mvc.perform(
            patch(url("/estado"))
                .contentType("application/json")
                .content("{\"estado\":\"VENDIDA\"}"))
        .andExpect(status().isConflict());
    mvc.perform(delete(url("/reserva")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    mvc.perform(
            patch(url("/estado"))
                .contentType("application/json")
                .content("{\"estado\":\"EN_RESTAURACION\"}"))
        .andExpect(status().isOk());
    mvc.perform(
            post(url("/ventas"))
                .contentType("application/json")
                .content(json.writeValueAsString(pedido())))
        .andExpect(status().isConflict());
    assertThat(operaciones.historial(obra.id())).hasSize(3);
  }

  @Test
  void validacionHttpYDuplicados() throws Exception {
    mvc.perform(post("/api/artistas").contentType("application/json").content("{}"))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post("/api/artistas")
                .contentType("application/json")
                .content(
                    json.writeValueAsString(
                        new ArtistaAlta(
                            artista.dni(),
                            "Otra",
                            "Persona",
                            null,
                            TipoComision.PORCENTAJE,
                            BigDecimal.ZERO))))
        .andExpect(status().isConflict());
    mvc.perform(
            post(url("/ventas"))
                .contentType("application/json")
                .content("{\"compradorId\":" + comprador.id() + ",\"adicionales\":[null]}"))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post(url("/ventas"))
                .contentType("application/json")
                .content(
                    "{\"compradorId\":"
                        + comprador.id()
                        + ",\"adicionales\":[{\"tipo\":\"ENVIO\",\"importe\":-1}]}"))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/api/obras/99999999")).andExpect(status().isNotFound());
    mvc.perform(
            patch(url("/estado")).contentType("application/json").content("{\"estado\":\"OTRO\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void falloDelObservadorRevierteVentaEstadoEHistorial() {
    doThrow(new IllegalStateException("Fallo simulado")).when(observador).alOcurrir(any());
    try {
      assertThatThrownBy(() -> operaciones.vender(obra.id(), pedido()))
          .isInstanceOf(IllegalStateException.class);
    } finally {
      reset(observador);
    }
    assertThat(catalogo.obra(obra.id()).estado()).isEqualTo(EstadoObra.DISPONIBLE);
    assertThat(ventas.findByObraId(obra.id())).isEmpty();
    assertThat(operaciones.historial(obra.id())).isEmpty();
    assertThat(catalogo.notificaciones(artista.id())).isEmpty();
  }

  @Test
  void dosVentasConcurrentesProducenUnaSolaVenta() throws Exception {
    ExecutorService pool = Executors.newFixedThreadPool(2);
    CountDownLatch inicio = new CountDownLatch(1);
    Callable<Boolean> intento =
        () -> {
          inicio.await();
          try {
            operaciones.vender(obra.id(), pedido());
            return true;
          } catch (NegocioException | org.springframework.dao.ConcurrencyFailureException e) {
            return false;
          }
        };
    try {
      Future<Boolean> a = pool.submit(intento), b = pool.submit(intento);
      inicio.countDown();
      assertThat(List.of(a.get(15, TimeUnit.SECONDS), b.get(15, TimeUnit.SECONDS)))
          .containsExactlyInAnyOrder(true, false);
    } finally {
      pool.shutdownNow();
    }
    assertThat(ventas.findByObraId(obra.id())).isPresent();
    assertThat(operaciones.historial(obra.id())).hasSize(1);
    assertThat(catalogo.notificaciones(comprador.id())).hasSize(1);
  }

  @Test
  void rechazaConsignacionVencidaYSinConsignacion() {
    Lugar lugar = catalogo.crearUbicacion(new UbicacionAlta("Otro", null));
    Pieza sin =
        catalogo.crearObra(
            new ObraAlta(
                "Otra", null, null, null, artista.id(), lugar.id(), new BigDecimal("10"), null));
    assertThatThrownBy(() -> operaciones.vender(sin.id(), pedido()))
        .isInstanceOf(NegocioException.class)
        .hasMessageContaining("consignación");
    catalogo.crearConsignacion(
        new ConsignacionAlta(
            sin.id(), LocalDate.now().minusDays(2), LocalDate.now().minusDays(1), null, null));
    assertThatThrownBy(() -> operaciones.vender(sin.id(), pedido()))
        .isInstanceOf(NegocioException.class)
        .hasMessageContaining("vigente");
    assertThat(operaciones.historial(sin.id())).isEmpty();
  }

  @Test
  void altasPorHttpYConsultasNoExponenEntidades() throws Exception {
    String dni = UUID.randomUUID().toString();
    long artistaId =
        alta(
            "/artistas",
            new ArtistaAlta(
                dni, "Ana", "Luz", null, TipoComision.IMPORTE_FIJO, new BigDecimal("20")));
    alta("/compradores", new CompradorAlta(dni + "x", "Luis", "Mar"));
    long lugarId = alta("/ubicaciones", new UbicacionAlta("Sala HTTP", "Calle de ejemplo"));
    long obraId =
        alta(
            "/obras",
            new ObraAlta(
                "HTTP", 2026, null, null, artistaId, lugarId, new BigDecimal("100"), List.of()));
    alta(
        "/consignaciones",
        new ConsignacionAlta(obraId, LocalDate.now().minusDays(1), null, null, null));
    for (String recurso :
        List.of(
            "/artistas", "/compradores", "/ubicaciones", "/obras", "/consignaciones", "/ventas"))
      mvc.perform(get("/api" + recurso)).andExpect(status().isOk());
    mvc.perform(get("/api/obras/" + obraId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.artistaId").value(artistaId))
        .andExpect(jsonPath("$.artista").doesNotExist());
  }

  private long alta(String ruta, Object cuerpo) throws Exception {
    String respuesta =
        mvc.perform(
                post("/api" + ruta)
                    .contentType("application/json")
                    .content(json.writeValueAsString(cuerpo)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return json.readTree(respuesta).get("id").asLong();
  }

  @Test
  void adicionalesDuplicadosNoDejanEfectos() {
    VentaAlta duplicada =
        new VentaAlta(
            comprador.id(),
            List.of(
                new Adicional(TipoAdicional.ENVIO, BigDecimal.ONE),
                new Adicional(TipoAdicional.ENVIO, BigDecimal.TEN)));
    assertThatThrownBy(() -> operaciones.vender(obra.id(), duplicada))
        .isInstanceOf(NegocioException.class);
    assertThat(operaciones.historial(obra.id())).isEmpty();
    assertThat(catalogo.obra(obra.id()).estado()).isEqualTo(EstadoObra.DISPONIBLE);
  }

  @Test
  void errorInesperadoNoExponeDetallesYRespetaEstadosHttp() throws Exception {
    doThrow(new IllegalStateException("detalle confidencial simulado"))
        .when(observador)
        .alOcurrir(any());
    try {
      mvc.perform(
              post(url("/ventas"))
                  .contentType("application/json")
                  .content(json.writeValueAsString(pedido())))
          .andExpect(status().isInternalServerError())
          .andExpect(jsonPath("$.detail").value("Error interno del servidor"))
          .andExpect(jsonPath("$.referencia").isNotEmpty())
          .andExpect(
              content()
                  .string(
                      org.hamcrest.Matchers.not(
                          org.hamcrest.Matchers.containsString("detalle confidencial"))));
    } finally {
      reset(observador);
    }
    assertThat(catalogo.obra(obra.id()).estado()).isEqualTo(EstadoObra.DISPONIBLE);
    assertThat(operaciones.historial(obra.id())).isEmpty();
    mvc.perform(get("/api/recurso-inexistente")).andExpect(status().isNotFound());
    mvc.perform(put("/api/obras"))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(header().string("Allow", org.hamcrest.Matchers.containsString("GET")));
  }
}
