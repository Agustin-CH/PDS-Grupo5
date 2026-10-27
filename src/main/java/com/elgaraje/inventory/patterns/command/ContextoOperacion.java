package com.elgaraje.inventory.patterns.command;

import com.elgaraje.inventory.models.*;
import com.elgaraje.inventory.patterns.state.CicloObra;

public record ContextoOperacion(
    Obra obra, Comprador comprador, Ubicacion ubicacion, CicloObra ciclo) {}
