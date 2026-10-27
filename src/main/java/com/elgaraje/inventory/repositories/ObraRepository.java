package com.elgaraje.inventory.repositories;

import com.elgaraje.inventory.models.Obra;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ObraRepository extends JpaRepository<Obra, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select o from Obra o where o.id = :id")
  Optional<Obra> buscarParaActualizar(@Param("id") Long id);
}
