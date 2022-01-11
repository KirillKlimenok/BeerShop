package com.modsen.repository.entytie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Beer {
    private int id;
    private String name;
    private int idContainer;
    private int idTypeBeer;
    private float alcoholContent;
    private int ibu;
    private String countBeerJson;
}
