package com.paymentchain.customer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.business.transactions.BusinessTransaction;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exception.BussinesRuleException;
import com.paymentchain.customer.respository.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.net.UnknownHostException;
import java.util.Collections;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;


@RestController
@RequestMapping("/customer")
public class CustomerRestController {

    @Autowired
    private BusinessTransaction bt;

    @Autowired
    CustomerRepository customerRepository;

    @Value("${user.role}")
    private String role;
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello your role is: " + role;
    }

    @GetMapping("/full")
    public Customer get(@RequestParam String code) throws BussinesRuleException, UnknownHostException {
        Customer customer = bt.get(code);

        return customer;
    }

    @GetMapping()
    public ResponseEntity<List<Customer>> list() {
        List<Customer> findAll  = customerRepository.findAll();

        if(findAll.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(findAll);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> get(@PathVariable long id) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Customer input) {
        return customerRepository.findById(id)
                .map(customerFound -> {
                    customerFound.setCode(input.getCode());
                    customerFound.setAddress(input.getAddress());
                    customerFound.setIban(input.getIban());
                    customerFound.setName(input.getName());
                    customerFound.setPhone(input.getPhone());
                    customerFound.setSurname(input.getSurname());

                    return new ResponseEntity<>(customerRepository.save(customerFound), HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer input) throws BussinesRuleException, UnknownHostException {
        Customer save = bt.saveAndVerifyIfCustomerExists(input);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(customerFound -> {
                    customerRepository.delete(customerFound);

                    return new ResponseEntity<>(HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
