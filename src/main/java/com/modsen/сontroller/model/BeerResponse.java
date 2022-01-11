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
    String name;
    String container;
    float volume;
    String beerType;
    float alcoholContent;
    int ibu;
    String count;
}
