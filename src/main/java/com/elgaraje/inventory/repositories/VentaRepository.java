package com.elgaraje.inventory.repositories;

import com.elgaraje.inventory.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
  java.util.Optional<Venta> findByObraId(Long obraId);
}
