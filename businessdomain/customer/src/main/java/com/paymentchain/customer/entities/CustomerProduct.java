package com.paymentchain.customer.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Transient;
import lombok.Data;



@Entity
@Data
public class CustomerProduct {
 
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private long productId;
    @Transient
    private String productName;
    @JsonIgnore//it is necesary for avoid infinite recursion
    @ManyToOne(fetch = FetchType.LAZY,targetEntity = Customer.class)
    @JoinColumn(name = "customerId", nullable = true)   
    private Customer customer;     
    
}