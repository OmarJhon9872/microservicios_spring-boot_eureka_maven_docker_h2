package com.paymentchain.transactions.respository;


import com.paymentchain.transactions.entities.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
      @Query("SELECT t FROM Transaction t WHERE t.ibanAccount = ?1")
      public List<Transaction> findByIbanAccount(String ibanAccount);
      
}
