package com.elgaraje.inventory.controllers;

import com.elgaraje.inventory.dtos.Respuestas.*;
import com.elgaraje.inventory.dtos.Solicitudes.*;
import com.elgaraje.inventory.services.OperacionesServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OperacionesController {
  private final OperacionesServicio servicio;

  public OperacionesController(OperacionesServicio servicio) {
    this.servicio = servicio;
  }

  @PostMapping("/obras/{id}/reservas")
  public Pieza reservar(@PathVariable Long id, @Valid @RequestBody Reserva r) {
    return servicio.reservar(id, r);
  }

  @DeleteMapping("/obras/{id}/reserva")
  public Pieza cancelar(@PathVariable Long id) {
    return servicio.cancelar(id);
  }

  @PostMapping("/obras/{id}/traslados")
  public Pieza trasladar(@PathVariable Long id, @Valid @RequestBody Traslado r) {
    return servicio.trasladar(id, r);
  }

  @PatchMapping("/obras/{id}/estado")
  public Pieza estado(@PathVariable Long id, @Valid @RequestBody Transicion r) {
    return servicio.cambiarEstado(id, r);
  }

  @PostMapping("/obras/{id}/cotizacion")
  public VentaDetalle cotizar(@PathVariable Long id, @Valid @RequestBody VentaAlta r) {
    return servicio.cotizar(id, r);
  }

  @PostMapping("/obras/{id}/ventas")
  @ResponseStatus(HttpStatus.CREATED)
  public VentaDetalle vender(@PathVariable Long id, @Valid @RequestBody VentaAlta r) {
    return servicio.vender(id, r);
  }

  @GetMapping("/obras/{id}/historial")
  public List<Accion> historial(@PathVariable Long id) {
    return servicio.historial(id);
  }

  @GetMapping("/ventas")
  public List<VentaDetalle> ventas() {
    return servicio.ventas();
  }

  @GetMapping("/ventas/{id}")
  public VentaDetalle venta(@PathVariable Long id) {
    return servicio.venta(id);
  }
}
