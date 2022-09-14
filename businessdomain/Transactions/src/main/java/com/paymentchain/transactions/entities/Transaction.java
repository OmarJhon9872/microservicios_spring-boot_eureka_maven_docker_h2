package com.paymentchain.transactions.entities;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;


@Data
@Entity
public class Transaction {
    
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
   private long id;
   private String reference;
   private String ibanAccount;
   private LocalDateTime date;
   private double amount ;
   private double fee;   
   private String description;
   private String status;
   private String channel;   
}
