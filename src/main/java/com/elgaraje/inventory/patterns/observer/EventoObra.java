package com.elgaraje.inventory.patterns.observer;

import java.time.Instant;

public record EventoObra(
    Long obraId, String tipo, String mensaje, Long compradorId, Instant fecha) {}
