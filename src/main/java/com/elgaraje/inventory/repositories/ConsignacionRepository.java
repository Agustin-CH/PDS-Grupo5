package com.elgaraje.inventory.repositories;

import com.elgaraje.inventory.models.Consignacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsignacionRepository extends JpaRepository<Consignacion, Long> {
  java.util.Optional<Consignacion> findByObraId(Long obraId);
}
