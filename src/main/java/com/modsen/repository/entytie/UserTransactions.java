package com.modsen.repository.entytie;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class UserTransactions {
   private long id_beer;
   private int count;
   private Date date_time;
}
