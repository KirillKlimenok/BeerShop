package com.modsen.сontroller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeerResponse {
    int id;
    String name;
    String container;
    Float volume;
    String beerType;
    Float alcoholContent;
    Integer ibu;
    String count;

    public BeerResponse(String name, String container, Float volume, String beerType, Float alcoholContent, Integer ibu, String count) {
        this.name = name;
        this.container = container;
        this.volume = volume;
        this.beerType = beerType;
        this.alcoholContent = alcoholContent;
        this.ibu = ibu;
        this.count = count;
    }
}
