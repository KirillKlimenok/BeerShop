package com.modsen.service;

import com.modsen.exception.BeerValidationException;
import lombok.Builder;

import java.util.List;
import java.util.function.Function;

@Builder
public class BeerVolumeValidatorService<T> implements Validator<T> {
    private Function<T, Float> getVolume;
    private Function<T, String> getType;
    private List<String> nameContainerBeerWithoutConditions;
    private float min;
    private float max;

    @Override
    public void check(T obj) {
        String currentType = getType.apply(obj);

        if (currentType == null) {
            throw new BeerValidationException("you don't entered type");
        }

        if (nameContainerBeerWithoutConditions.stream().noneMatch(currentType::equals)) {
            float currentVolume = getVolume.apply(obj);

            if (currentVolume == 0) {
                throw new BeerValidationException("you don't entered volume");
            }

            if (currentVolume < min) {
                throw new BeerValidationException("You entered vary small volume");
            } else if (currentVolume > max) {
                throw new BeerValidationException("You entered vary big volume");
            }
        }
    }
}
