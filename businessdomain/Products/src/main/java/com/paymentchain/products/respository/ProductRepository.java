package com.paymentchain.products.respository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.paymentchain.products.entities.Product;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
   @Query("SELECT p FROM Product p WHERE p.code = ?1")
    public Product findByCode(String code);
}
