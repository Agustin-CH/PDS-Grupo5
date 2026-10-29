package com.elgaraje.inventory.repositories;

import com.elgaraje.inventory.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
  java.util.List<Notificacion> findByDestinatarioIdOrderByFechaAscIdAsc(Long destinatarioId);
}
