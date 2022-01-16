package com.modsen.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BeerDto {
    private String name;
    private Integer idContainer;
    private Integer idBeerType;
    private Float alcoholContent;
    private Integer ibu;
    private Integer countBeer;

    public BeerDto(String name, Integer idContainer, Integer idBeerType, Float alcoholContent, Integer ibu) {
        this.name = name;
        this.idContainer = idContainer;
        this.idBeerType = idBeerType;
        this.alcoholContent = alcoholContent;
        this.ibu = ibu;
        this.countBeer = 0;
    }
}
