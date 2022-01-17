package com.modsen.repository.entytie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class AdminTransaction {
    private UUID userId;
    private Integer idBeer;
    private int count;
    private LocalDateTime dateTime;
}
