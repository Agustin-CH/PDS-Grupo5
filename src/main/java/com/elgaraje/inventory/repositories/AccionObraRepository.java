package com.elgaraje.inventory.repositories;

import com.elgaraje.inventory.models.AccionObra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccionObraRepository extends JpaRepository<AccionObra, Long> {
  java.util.List<AccionObra> findByObraIdOrderByFechaAscIdAsc(Long obraId);
}
