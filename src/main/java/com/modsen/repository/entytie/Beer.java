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
    private Integer idContainer;
    private int idTypeBeer;
    private float alcoholContent;
    private int ibu;
    private String count;

    public void setParam(Integer idContainer, String countBeerJson) {
        if (idContainer != null) {
            this.idContainer = idContainer;
        }

        if (countBeerJson != null) {
            this.count = countBeerJson;
        }
    }
}
