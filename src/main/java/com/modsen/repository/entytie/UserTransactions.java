package com.modsen.repository.entytie;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@AllArgsConstructor
public class UserTransactions {
   private Integer id_beer;
   private int count;
   private Date date_time;
}
