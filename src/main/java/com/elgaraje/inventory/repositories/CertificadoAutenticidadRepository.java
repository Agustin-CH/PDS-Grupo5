package com.elgaraje.inventory.repositories;

import com.elgaraje.inventory.models.CertificadoAutenticidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificadoAutenticidadRepository
    extends JpaRepository<CertificadoAutenticidad, Long> {
  java.util.Optional<CertificadoAutenticidad> findByObraId(Long obraId);
}
