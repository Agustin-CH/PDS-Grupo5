package com.elgaraje.inventory.services;

import com.elgaraje.inventory.dtos.Respuestas.*;
import com.elgaraje.inventory.dtos.Solicitudes.*;
import com.elgaraje.inventory.errors.*;
import com.elgaraje.inventory.models.*;
import com.elgaraje.inventory.patterns.command.*;
import com.elgaraje.inventory.patterns.decorator.*;
import com.elgaraje.inventory.patterns.factory.CreadorVenta;
import com.elgaraje.inventory.patterns.state.CicloObra;
import com.elgaraje.inventory.repositories.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OperacionesServicio {
  private final ObraRepository obras;
  private final CompradorRepository compradores;
  private final UbicacionRepository ubicaciones;
  private final ConsignacionRepository consignaciones;
  private final VentaRepository ventas;
  private final AccionObraRepository acciones;
  private final EjecutorComandos ejecutor;
  private final CicloObra ciclo;
  private final CreadorVenta creador;
  private final Clock reloj;

  public OperacionesServicio(
      ObraRepository obras,
      CompradorRepository compradores,
      UbicacionRepository ubicaciones,
      ConsignacionRepository consignaciones,
      VentaRepository ventas,
      AccionObraRepository acciones,
      EjecutorComandos ejecutor,
      CicloObra ciclo,
      CreadorVenta creador,
      Clock reloj) {
    this.obras = obras;
    this.compradores = compradores;
    this.ubicaciones = ubicaciones;
    this.consignaciones = consignaciones;
    this.ventas = ventas;
    this.acciones = acciones;
    this.ejecutor = ejecutor;
    this.ciclo = ciclo;
    this.creador = creador;
    this.reloj = reloj;
  }

  private Obra bloquear(Long id) {
    return obras.buscarParaActualizar(id).orElseThrow(() -> new NoEncontradoException("Obra"));
  }

  private Comprador comprador(Long id) {
    return compradores.findById(id).orElseThrow(() -> new NoEncontradoException("Comprador"));
  }

  @Transactional
  public Pieza reservar(Long id, Reserva r) {
    Obra o = bloquear(id);
    ejecutor.ejecutar(
        new ReservarObra(), new ContextoOperacion(o, comprador(r.compradorId()), null, ciclo));
    return Pieza.de(o);
  }

  @Transactional
  public Pieza cancelar(Long id) {
    Obra o = bloquear(id);
    ejecutor.ejecutar(new CancelarReserva(), new ContextoOperacion(o, null, null, ciclo));
    return Pieza.de(o);
  }

  @Transactional
  public Pieza trasladar(Long id, Traslado r) {
    Obra o = bloquear(id);
    Ubicacion u =
        ubicaciones
            .findById(r.ubicacionId())
            .orElseThrow(() -> new NoEncontradoException("Ubicación"));
    ejecutor.ejecutar(new TrasladarObra(), new ContextoOperacion(o, null, u, ciclo));
    return Pieza.de(o);
  }

  @Transactional
  public Pieza cambiarEstado(Long id, Transicion r) {
    Obra o = bloquear(id);
    ejecutor.ejecutar(new CambiarEstado(r.estado()), new ContextoOperacion(o, null, null, ciclo));
    return Pieza.de(o);
  }

  @Transactional
  public VentaDetalle vender(Long id, VentaAlta r) {
    Obra o = bloquear(id);
    Comprador c = comprador(r.compradorId());
    Venta venta = preparar(o, c, r);
    ejecutor.ejecutar(new VenderObra(), new ContextoOperacion(o, c, null, ciclo));
    return VentaDetalle.de(ventas.saveAndFlush(venta));
  }

  public VentaDetalle cotizar(Long id, VentaAlta r) {
    Obra o = obras.findById(id).orElseThrow(() -> new NoEncontradoException("Obra"));
    return VentaDetalle.de(preparar(o, comprador(r.compradorId()), r));
  }

  private Venta preparar(Obra o, Comprador c, VentaAlta r) {
    new VenderObra().validar(new ContextoOperacion(o, c, null, ciclo));
    if (ventas.findByObraId(o.getId()).isPresent())
      throw new NegocioException("La obra ya tiene una venta");
    Consignacion consignacion =
        consignaciones
            .findByObraId(o.getId())
            .orElseThrow(() -> new NegocioException("La obra requiere consignación"));
    LocalDate hoy = LocalDate.now(reloj);
    if (consignacion.getFecha().isAfter(hoy)
        || (consignacion.getVencimiento() != null && consignacion.getVencimiento().isBefore(hoy)))
      throw new NegocioException("La consignación no está vigente");
    if (o.getPrecio() == null || o.getPrecio().signum() <= 0)
      throw new NegocioException("La obra requiere un precio positivo");
    CostoVenta costo = new PrecioBase(o.getPrecio());
    Set<TipoAdicional> agregados = EnumSet.noneOf(TipoAdicional.class);
    for (Adicional a : r.adicionales() == null ? List.<Adicional>of() : r.adicionales()) {
      if (!agregados.add(a.tipo())) throw new NegocioException("Adicional repetido: " + a.tipo());
      costo =
          switch (a.tipo()) {
            case ENMARCADO -> new Enmarcado(costo, a.importe());
            case ENVIO -> new Envio(costo, a.importe());
            case SEGURO -> new Seguro(costo, a.importe());
          };
    }
    if (costo.total().precision() - costo.total().scale() > 17)
      throw new NegocioException("El total supera la capacidad monetaria");
    return creador.preparar(consignacion, c, costo, reloj.instant());
  }

  public List<Accion> historial(Long id) {
    if (!obras.existsById(id)) throw new NoEncontradoException("Obra");
    return acciones.findByObraIdOrderByFechaAscIdAsc(id).stream().map(Accion::de).toList();
  }

  public List<VentaDetalle> ventas() {
    return ventas.findAll().stream().map(VentaDetalle::de).toList();
  }

  public VentaDetalle venta(Long id) {
    return VentaDetalle.de(
        ventas.findById(id).orElseThrow(() -> new NoEncontradoException("Venta")));
  }
}
