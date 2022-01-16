package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.apiguardian.api.API;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;


public class TenmoService {

    // call to server side
    private RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;

    String API_BASE_URL = "http://localhost:8080/";


    // whenever user logs in, we want to set the auth token
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public BigDecimal retrieveBalance() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Balance balance = restTemplate.exchange(API_BASE_URL + "balance", HttpMethod.GET, entity, Balance.class).getBody();

        return balance.getBalance();
    }

    // call transfer funds

    public Transfer transferFromBalance(int userIdTo, BigDecimal amount) {

        Transfer transfer = new Transfer();

        transfer.setAccountTo(userIdTo);
        transfer.setAmount(amount);
        // we set these because they will be defaulted to a transfer status of
        // Pending and the type is sending
        transfer.setTransferStatusID(1);
        transfer.setTransferTypeId(2);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.authToken);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

        Transfer updatedTransfer = restTemplate.exchange(API_BASE_URL + "transfer", HttpMethod.PUT, entity, Transfer.class).getBody();

        return updatedTransfer;

    }

    public User[] listUsers() {
        User[] hotels = null;
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL + "users", HttpMethod.GET,
                    makeAuthEntity(), User[].class);
            hotels = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            e.getMessage();
        }
        return hotels;
    }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    public Transfer[] listTransfers(){
        Transfer[] transfers = null;

        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "transfers", HttpMethod.GET,
                    makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            e.getMessage();
        }
        return transfers;
    }

    public int getAccountId() {
        ResponseEntity<Integer> response = restTemplate.exchange(API_BASE_URL + "account", HttpMethod.GET, makeAuthEntity(), Integer.class);
        return response.getBody();
    }

    public User getUser(User user){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.authToken);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);

        User userWithName = restTemplate.exchange(API_BASE_URL + "user", HttpMethod.PUT, entity, User.class).getBody();
        return userWithName;
    }


}
