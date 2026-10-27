package com.elgaraje.inventory.services;

import com.elgaraje.inventory.dtos.Respuestas.*;
import com.elgaraje.inventory.dtos.Solicitudes.*;
import com.elgaraje.inventory.errors.*;
import com.elgaraje.inventory.models.*;
import com.elgaraje.inventory.patterns.strategy.CalculadoraComision;
import com.elgaraje.inventory.repositories.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogoServicio {
  private final ArtistaRepository artistas;
  private final CompradorRepository compradores;
  private final ObraRepository obras;
  private final UbicacionRepository ubicaciones;
  private final ConsignacionRepository consignaciones;
  private final CertificadoAutenticidadRepository certificados;
  private final UsuarioRepository usuarios;
  private final NotificacionRepository notificaciones;
  private final CalculadoraComision calculadora;

  public CatalogoServicio(
      ArtistaRepository artistas,
      CompradorRepository compradores,
      ObraRepository obras,
      UbicacionRepository ubicaciones,
      ConsignacionRepository consignaciones,
      CertificadoAutenticidadRepository certificados,
      UsuarioRepository usuarios,
      NotificacionRepository notificaciones,
      CalculadoraComision calculadora) {
    this.artistas = artistas;
    this.compradores = compradores;
    this.obras = obras;
    this.ubicaciones = ubicaciones;
    this.consignaciones = consignaciones;
    this.certificados = certificados;
    this.usuarios = usuarios;
    this.notificaciones = notificaciones;
    this.calculadora = calculadora;
  }

  @Transactional
  public Persona crearArtista(ArtistaAlta r) {
    calculadora.validarCondiciones(r.tipoComision(), r.valorComision());
    Artista a = new Artista();
    a.setDni(r.dni());
    a.setNombre(r.nombre());
    a.setApellido(r.apellido());
    a.setBiografia(r.biografia());
    a.setTipoComision(r.tipoComision());
    a.setValorComision(r.valorComision());
    return Persona.de(artistas.saveAndFlush(a));
  }

  @Transactional
  public Persona crearComprador(CompradorAlta r) {
    Comprador c = new Comprador();
    c.setDni(r.dni());
    c.setNombre(r.nombre());
    c.setApellido(r.apellido());
    return Persona.de(compradores.saveAndFlush(c));
  }

  @Transactional
  public Lugar crearUbicacion(UbicacionAlta r) {
    Ubicacion u = new Ubicacion();
    u.setNombre(r.nombre());
    u.setDireccion(r.direccion());
    return Lugar.de(ubicaciones.saveAndFlush(u));
  }

  @Transactional
  public Pieza crearObra(ObraAlta r) {
    Obra o = new Obra();
    o.setTitulo(r.titulo());
    o.setAnio(r.anio());
    o.setDimensiones(r.dimensiones());
    o.setDescripcion(r.descripcion());
    o.setPrecio(r.precio());
    o.setArtista(
        artistas.findById(r.artistaId()).orElseThrow(() -> new NoEncontradoException("Artista")));
    o.setUbicacion(
        ubicaciones
            .findById(r.ubicacionId())
            .orElseThrow(() -> new NoEncontradoException("Ubicación")));
    o.setFotos(r.fotos() == null ? new ArrayList<>() : new ArrayList<>(r.fotos()));
    return Pieza.de(obras.saveAndFlush(o));
  }

  @Transactional
  public Consignada crearConsignacion(ConsignacionAlta r) {
    Obra o =
        obras.buscarParaActualizar(r.obraId()).orElseThrow(() -> new NoEncontradoException("Obra"));
    if (o.getEstado() == EstadoObra.VENDIDA) throw new NegocioException("La obra ya está vendida");
    if (o.getArtista() == null) throw new NegocioException("La obra requiere un artista");
    if (consignaciones.findByObraId(o.getId()).isPresent())
      throw new NegocioException("La obra ya tiene consignación");
    if (r.vencimiento() != null && r.vencimiento().isBefore(r.fecha()))
      throw new NegocioException("Vencimiento anterior al inicio");
    if ((r.tipoComision() == null) != (r.valorComision() == null))
      throw new NegocioException("Informe tipo y valor de comisión juntos");
    if (r.tipoComision() != null)
      calculadora.validarCondiciones(r.tipoComision(), r.valorComision());
    Consignacion c = new Consignacion();
    c.setObra(o);
    c.setArtista(o.getArtista());
    c.setFecha(r.fecha());
    c.setVencimiento(r.vencimiento());
    c.setTipoComision(r.tipoComision());
    c.setValorComision(r.valorComision());
    return Consignada.de(consignaciones.saveAndFlush(c));
  }

  @Transactional
  public Certificado crearCertificado(CertificadoAlta r) {
    Obra o =
        obras.buscarParaActualizar(r.obraId()).orElseThrow(() -> new NoEncontradoException("Obra"));
    if (certificados.findByObraId(o.getId()).isPresent())
      throw new NegocioException("La obra ya tiene certificado");
    CertificadoAutenticidad c = new CertificadoAutenticidad();
    c.setObra(o);
    c.setNumero(r.numero());
    c.setFechaEmision(r.fechaEmision());
    c.setEmisor(r.emisor());
    c.setFirma(r.firma());
    return Certificado.de(certificados.saveAndFlush(c));
  }

  public List<Persona> artistas() {
    return artistas.findAll().stream().map(Persona::de).toList();
  }

  public List<Persona> compradores() {
    return compradores.findAll().stream().map(Persona::de).toList();
  }

  public List<Lugar> ubicaciones() {
    return ubicaciones.findAll().stream().map(Lugar::de).toList();
  }

  public List<Pieza> obras() {
    return obras.findAll().stream().map(Pieza::de).toList();
  }

  public Pieza obra(Long id) {
    return Pieza.de(obras.findById(id).orElseThrow(() -> new NoEncontradoException("Obra")));
  }

  public List<Consignada> consignaciones() {
    return consignaciones.findAll().stream().map(Consignada::de).toList();
  }

  public Certificado certificado(Long obraId) {
    return Certificado.de(
        certificados
            .findByObraId(obraId)
            .orElseThrow(() -> new NoEncontradoException("Certificado")));
  }

  public List<Aviso> notificaciones(Long usuarioId) {
    if (!usuarios.existsById(usuarioId)) throw new NoEncontradoException("Usuario");
    return notificaciones.findByDestinatarioIdOrderByFechaAscIdAsc(usuarioId).stream()
        .map(Aviso::de)
        .toList();
  }
}
