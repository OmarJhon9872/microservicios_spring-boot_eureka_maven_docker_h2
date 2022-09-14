package com.paymentchain.products.controller;

import com.paymentchain.products.respository.ProductRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.paymentchain.products.entities.Product;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/product")
public class ProductRestController {
    
    @Autowired
    ProductRepository customerRepository;
         
     @Value("${user.role}")
    private String role;
    
     @GetMapping()
    public List<Product> list() {
         System.out.print("el role es : " +role);
        return customerRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Product get(@PathVariable long id) {   
        Product customer = customerRepository.findById(id).get();         
        return customer;   
    }  
   
    
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable String id, @RequestBody Product input) {
        return null;
    }
    
   @PostMapping
    public ResponseEntity<?> post(@RequestBody Product input) {       
        Product save = customerRepository.save(input);
        return ResponseEntity.ok(save);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return null;
    }
    
}
