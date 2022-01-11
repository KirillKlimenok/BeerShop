package com.modsen.repository.entytie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTransactions {
   private Integer idBeer;
   private int count;
   private LocalDateTime dateTime;
}
