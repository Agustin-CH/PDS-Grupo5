package com.elgaraje.inventory.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class Comprador extends Usuario {

    @OneToMany(mappedBy = "comprador")
    private List<Obra> obrasAdquiridas;
}