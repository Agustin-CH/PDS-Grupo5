package com.elgaraje.inventory.controllers;

import com.elgaraje.inventory.dtos.Respuestas.*;
import com.elgaraje.inventory.dtos.Solicitudes.*;
import com.elgaraje.inventory.services.CatalogoServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CatalogoController {
  private final CatalogoServicio servicio;

  public CatalogoController(CatalogoServicio servicio) {
    this.servicio = servicio;
  }

  @PostMapping("/artistas")
  @ResponseStatus(HttpStatus.CREATED)
  public Persona artista(@Valid @RequestBody ArtistaAlta r) {
    return servicio.crearArtista(r);
  }

  @GetMapping("/artistas")
  public List<Persona> artistas() {
    return servicio.artistas();
  }

  @PostMapping("/compradores")
  @ResponseStatus(HttpStatus.CREATED)
  public Persona comprador(@Valid @RequestBody CompradorAlta r) {
    return servicio.crearComprador(r);
  }

  @GetMapping("/compradores")
  public List<Persona> compradores() {
    return servicio.compradores();
  }

  @PostMapping("/ubicaciones")
  @ResponseStatus(HttpStatus.CREATED)
  public Lugar ubicacion(@Valid @RequestBody UbicacionAlta r) {
    return servicio.crearUbicacion(r);
  }

  @GetMapping("/ubicaciones")
  public List<Lugar> ubicaciones() {
    return servicio.ubicaciones();
  }

  @PostMapping("/obras")
  @ResponseStatus(HttpStatus.CREATED)
  public Pieza obra(@Valid @RequestBody ObraAlta r) {
    return servicio.crearObra(r);
  }

  @GetMapping("/obras")
  public List<Pieza> obras() {
    return servicio.obras();
  }

  @GetMapping("/obras/{id}")
  public Pieza obra(@PathVariable Long id) {
    return servicio.obra(id);
  }

  @PostMapping("/consignaciones")
  @ResponseStatus(HttpStatus.CREATED)
  public Consignada consignacion(@Valid @RequestBody ConsignacionAlta r) {
    return servicio.crearConsignacion(r);
  }

  @GetMapping("/consignaciones")
  public List<Consignada> consignaciones() {
    return servicio.consignaciones();
  }

  @PostMapping("/certificados")
  @ResponseStatus(HttpStatus.CREATED)
  public Certificado certificado(@Valid @RequestBody CertificadoAlta r) {
    return servicio.crearCertificado(r);
  }

  @GetMapping("/obras/{id}/certificado")
  public Certificado certificado(@PathVariable Long id) {
    return servicio.certificado(id);
  }

  @GetMapping("/usuarios/{id}/notificaciones")
  public List<Aviso> notificaciones(@PathVariable Long id) {
    return servicio.notificaciones(id);
  }
}
