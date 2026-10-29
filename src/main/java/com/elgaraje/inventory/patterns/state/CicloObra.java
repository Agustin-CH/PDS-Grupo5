package com.elgaraje.inventory.patterns.state;

import com.elgaraje.inventory.errors.NegocioException;
import com.elgaraje.inventory.models.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class CicloObra {
  private final Map<EstadoObra, EstadoDeObra> estados = new EnumMap<>(EstadoObra.class);

  public CicloObra(List<EstadoDeObra> implementaciones) {
    implementaciones.forEach(e -> estados.put(e.codigo(), e));
  }

  public void cambiar(Obra obra, EstadoObra destino) {
    validar(obra, destino);
    obra.setEstado(destino);
  }

  public void validar(Obra obra, EstadoObra destino) {
    EstadoDeObra actual = estados.get(obra.getEstado());
    if (actual == null || destino == null) throw new NegocioException("Estado de obra inválido");
    actual.validar(destino);
  }
}
