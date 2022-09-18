package com.paymentchain.customer.business.transactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exception.BussinesRuleException;
import com.paymentchain.customer.respository.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BusinessTransaction {

    @Autowired
    private CustomerRepository customerRepository;
    private final WebClient.Builder webClientBuilder;

    public BusinessTransaction(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }


    //define timeout
    TcpClient tcpClient = TcpClient
            .create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    /*@GetMapping("/full")*/
    public Customer get(String code) throws BussinesRuleException, UnknownHostException{
        Customer customer = customerRepository.findByCode(code);

        if(customer == null){
            BussinesRuleException bre = new BussinesRuleException("1233", "El cliente no existe en la base de datos",   HttpStatus.PARTIAL_CONTENT);
            throw bre;
        }

        List<CustomerProduct> products = customer.getProducts();
        products.forEach(dto -> {
            String productName = null;
            try {
                productName = getProductName(dto.getProductId());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            dto.setProductName(productName);
        });
        customer.setTransactions(getTransacctions(customer.getIban()));
        return customer;
    }

    private <T> List<T> getTransacctions(String accountIban) {
        WebClient client = webClientBuilder.clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .baseUrl("http://businessdomain-transactions/transaction")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://businessdomain-transactions/transaction"))
                .build();
        List<Object> block = client.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder
                        .path("/transactions")
                        .queryParam("ibanAccount", accountIban)
                        .build())
                .retrieve().bodyToFlux(Object.class).collectList().block();
        List<T> name = (List<T>) block;
        return name;
    }


    private String getProductName(long id) throws UnknownHostException{
        String name = null;
        try{
            WebClient client = webClientBuilder.clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                    .baseUrl("http://businessdomain-product/product")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultUriVariables(Collections.singletonMap("url", "http://businessdomain-product/product"))
                    .build();
            JsonNode block = client.method(HttpMethod.GET).uri("/" + id)
                    .retrieve().bodyToMono(JsonNode.class).block();
            name = block.get("name").asText();
        }catch (WebClientResponseException e){
            HttpStatus httpStatus = e.getStatusCode();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> = " + httpStatus);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> = " + e.getStatusCode());
            if(httpStatus == HttpStatus.NOT_FOUND){
                return null;
            }
            throw new UnknownHostException(e.getMessage());
        }
        return name;
    }


    public Customer saveAndVerifyIfCustomerExists(Customer input) throws BussinesRuleException, UnknownHostException {
        for(Iterator<CustomerProduct> it = input.getProducts().iterator(); it.hasNext();){
            CustomerProduct dto = it.next();
            String productName = getProductName(dto.getProductId());

            if(productName == null){
                BussinesRuleException bre = new BussinesRuleException("999", "El producto no existe en la base de datos",   HttpStatus.PARTIAL_CONTENT);
                throw bre;
            }
            dto.setCustomer(input);
        }

        Customer save = customerRepository.save(input);
        return save;
    }

}
