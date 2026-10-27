package com.elgaraje.inventory.models;

import jakarta.persistence.Column;
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
public class Artista extends Usuario {

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @OneToMany(mappedBy = "artista")
    private List<Obra> obras;
}