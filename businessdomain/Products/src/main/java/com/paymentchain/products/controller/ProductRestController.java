package com.paymentchain.products.controller;

import com.paymentchain.products.respository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    ProductRepository productRepository;

    @Value("${user.role}")
    private String role;

    @GetMapping()
    public ResponseEntity<?> list() {
        System.out.print("el role es : " + role);
        List<Product> findAll  = productRepository.findAll();

        if(findAll.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(findAll);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Product input) {
        return productRepository.findById(id)
                .map(productFound -> {
                    Product product = productRepository.findByCode(input.getCode());

                    if(product != null && Objects.equals(product.getCode(), input.getCode()) && !Objects.equals(productFound.getCode(), input.getCode())){
                        return new ResponseEntity<>("Código de producto duplicado", HttpStatus.CONFLICT);
                    }
                    productFound.setCode(input.getCode());
                    productFound.setName(input.getName());

                    return new ResponseEntity<>(productRepository.save(productFound), HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Product input) {

        Product product = productRepository.findByCode(input.getCode());
        if(product == null){
            return new ResponseEntity<>(productRepository.save(input), HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Código de producto duplicado", HttpStatus.CONFLICT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(customerFound -> {
                    productRepository.delete(customerFound);

                    return new ResponseEntity<>(HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
