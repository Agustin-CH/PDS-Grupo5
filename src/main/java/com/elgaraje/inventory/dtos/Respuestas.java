package com.elgaraje.inventory.dtos;

import com.elgaraje.inventory.models.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;

public final class Respuestas {
  private Respuestas() {}

  public record Persona(
      Long id,
      String dni,
      String nombre,
      String apellido,
      String biografia,
      TipoComision tipoComision,
      BigDecimal valorComision) {
    public static Persona de(Usuario u) {
      Artista a = u instanceof Artista ? (Artista) u : null;
      return new Persona(
          u.getId(),
          u.getDni(),
          u.getNombre(),
          u.getApellido(),
          a == null ? null : a.getBiografia(),
          a == null ? null : a.getTipoComision(),
          a == null ? null : a.getValorComision());
    }
  }

  public record Lugar(Long id, String nombre, String direccion) {
    public static Lugar de(Ubicacion u) {
      return new Lugar(u.getId(), u.getNombre(), u.getDireccion());
    }
  }

  public record Pieza(
      Long id,
      String titulo,
      Integer anio,
      String dimensiones,
      String descripcion,
      EstadoObra estado,
      Long artistaId,
      Long compradorId,
      Long reservadoPorId,
      Long ubicacionId,
      BigDecimal precio,
      List<String> fotos) {
    public static Pieza de(Obra o) {
      return new Pieza(
          o.getId(),
          o.getTitulo(),
          o.getAnio(),
          o.getDimensiones(),
          o.getDescripcion(),
          o.getEstado(),
          o.getArtista() == null ? null : o.getArtista().getId(),
          o.getComprador() == null ? null : o.getComprador().getId(),
          o.getReservadoPor() == null ? null : o.getReservadoPor().getId(),
          o.getUbicacion() == null ? null : o.getUbicacion().getId(),
          o.getPrecio(),
          List.copyOf(o.getFotos()));
    }
  }

  public record Consignada(
      Long id,
      Long obraId,
      Long artistaId,
      LocalDate fecha,
      LocalDate vencimiento,
      TipoComision tipoComision,
      BigDecimal valorComision) {
    public static Consignada de(Consignacion c) {
      return new Consignada(
          c.getId(),
          c.getObra().getId(),
          c.getArtista().getId(),
          c.getFecha(),
          c.getVencimiento(),
          c.getTipoComision(),
          c.getValorComision());
    }
  }

  public record Certificado(
      Long id, Long obraId, String numero, LocalDate fechaEmision, String emisor, String firma) {
    public static Certificado de(CertificadoAutenticidad c) {
      return new Certificado(
          c.getId(),
          c.getObra().getId(),
          c.getNumero(),
          c.getFechaEmision(),
          c.getEmisor(),
          c.getFirma());
    }
  }

  public record VentaDetalle(
      Long id,
      Long obraId,
      Long consignacionId,
      Long compradorId,
      Instant fecha,
      BigDecimal precioObra,
      BigDecimal adicionales,
      BigDecimal monto,
      BigDecimal comisionGaleria,
      BigDecimal liquidacionArtista,
      TipoComision tipoComisionAplicada,
      BigDecimal valorComisionAplicado,
      List<String> servicios) {
    public static VentaDetalle de(Venta v) {
      return new VentaDetalle(
          v.getId(),
          v.getObra().getId(),
          v.getConsignacion().getId(),
          v.getComprador().getId(),
          v.getFecha(),
          v.getPrecioObra(),
          v.getAdicionales(),
          v.getMonto(),
          v.getComisionGaleria(),
          v.getLiquidacionArtista(),
          v.getTipoComisionAplicada(),
          v.getValorComisionAplicado(),
          List.copyOf(v.getServicios()));
    }
  }

  public record Accion(
      Long id,
      String tipo,
      Instant fecha,
      EstadoObra estadoAnterior,
      EstadoObra estadoNuevo,
      Long ubicacionAnteriorId,
      Long ubicacionNuevaId,
      String detalle) {
    public static Accion de(AccionObra a) {
      return new Accion(
          a.getId(),
          a.getTipo(),
          a.getFecha(),
          a.getEstadoAnterior(),
          a.getEstadoNuevo(),
          a.getUbicacionAnteriorId(),
          a.getUbicacionNuevaId(),
          a.getDetalle());
    }
  }

  public record Aviso(
      Long id, Long obraId, Long destinatarioId, String evento, String mensaje, Instant fecha) {
    public static Aviso de(Notificacion n) {
      return new Aviso(
          n.getId(),
          n.getObra().getId(),
          n.getDestinatario().getId(),
          n.getEvento(),
          n.getMensaje(),
          n.getFecha());
    }
  }
}
