package com.elgaraje.inventory.patterns.command;

import com.elgaraje.inventory.models.*;
import com.elgaraje.inventory.patterns.observer.EventoObra;
import com.elgaraje.inventory.repositories.AccionObraRepository;
import java.time.Clock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EjecutorComandos {
  private final AccionObraRepository historial;
  private final ApplicationEventPublisher eventos;
  private final Clock reloj;

  public EjecutorComandos(
      AccionObraRepository historial, ApplicationEventPublisher eventos, Clock reloj) {
    this.historial = historial;
    this.eventos = eventos;
    this.reloj = reloj;
  }

  public void ejecutar(ComandoObra comando, ContextoOperacion contexto) {
    Obra obra = contexto.obra();
    EstadoObra anterior = obra.getEstado();
    Long origen = obra.getUbicacion() == null ? null : obra.getUbicacion().getId();
    Long compradorId =
        contexto.comprador() != null
            ? contexto.comprador().getId()
            : obra.getReservadoPor() != null ? obra.getReservadoPor().getId() : null;
    comando.ejecutar(contexto);
    AccionObra accion = new AccionObra();
    accion.setObra(obra);
    accion.setTipo(comando.tipo());
    accion.setFecha(reloj.instant());
    accion.setEstadoAnterior(anterior);
    accion.setEstadoNuevo(obra.getEstado());
    accion.setUbicacionAnteriorId(origen);
    accion.setUbicacionNuevaId(obra.getUbicacion() == null ? null : obra.getUbicacion().getId());
    accion.setDetalle(comando.tipo() + " de obra " + obra.getId());
    historial.save(accion);
    eventos.publishEvent(
        new EventoObra(
            obra.getId(), comando.tipo(), accion.getDetalle(), compradorId, accion.getFecha()));
  }
}
