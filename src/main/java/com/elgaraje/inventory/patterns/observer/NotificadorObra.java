package com.elgaraje.inventory.patterns.observer;

import com.elgaraje.inventory.errors.NegocioException;
import com.elgaraje.inventory.errors.NoEncontradoException;
import com.elgaraje.inventory.models.*;
import com.elgaraje.inventory.repositories.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificadorObra {
  private final ObraRepository obras;
  private final UsuarioRepository usuarios;
  private final NotificacionRepository notificaciones;

  public NotificadorObra(
      ObraRepository obras, UsuarioRepository usuarios, NotificacionRepository notificaciones) {
    this.obras = obras;
    this.usuarios = usuarios;
    this.notificaciones = notificaciones;
  }

  // Listener síncrono: la notificación se confirma o revierte con la operación.
  @EventListener
  public void alOcurrir(EventoObra evento) {
    Obra obra =
        obras
            .findById(evento.obraId())
            .orElseThrow(() -> new NoEncontradoException("Obra del evento"));
    if (obra.getArtista() == null) throw new NegocioException("La obra requiere un artista");
    registrar(obra, obra.getArtista(), evento);
    if (evento.compradorId() != null)
      registrar(
          obra,
          usuarios
              .findById(evento.compradorId())
              .orElseThrow(() -> new NoEncontradoException("Destinatario del evento")),
          evento);
  }

  private void registrar(Obra obra, Usuario destinatario, EventoObra evento) {
    Notificacion n = new Notificacion();
    n.setObra(obra);
    n.setDestinatario(destinatario);
    n.setEvento(evento.tipo());
    n.setMensaje(evento.mensaje());
    n.setFecha(evento.fecha());
    notificaciones.save(n);
  }
}
