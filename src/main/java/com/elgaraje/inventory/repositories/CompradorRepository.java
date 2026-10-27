package com.elgaraje.inventory.repositories;

import com.elgaraje.inventory.models.Comprador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompradorRepository extends JpaRepository<Comprador, Long> {}
