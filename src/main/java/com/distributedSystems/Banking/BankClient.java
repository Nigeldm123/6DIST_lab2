package com.distributedSystems.Banking;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;

public class BankClient {

    private static final String BASE_URL = "http://localhost:8080"; // Change the URL accordingly

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        // Get all bank accounts
        ResponseEntity<String> getAllResponse = restTemplate.exchange(
                BASE_URL + "/bankAccounts",
                HttpMethod.GET,
                null,
                String.class);

        System.out.println("All Bank Accounts: " + getAllResponse.getBody());

        // Deposit to a bank account
        long accountId = 1; // Change the account ID accordingly
        double depositAmount = 50.0; // Change the deposit amount accordingly

        ResponseEntity<String> balanceResponse = restTemplate.exchange(
                BASE_URL + "/bankAccounts/{id}/balance",
                HttpMethod.GET,
                null,
                String.class,
                accountId);
        System.out.println("Balance Result: " + balanceResponse.getBody());

        // name to add
        String name = "Jeff";

        ResponseEntity<String> addResponse = restTemplate.postForEntity(
                BASE_URL + "/bankAccounts/{name}/add",
                null,
                String.class,
                name);

        System.out.println("added: " + name);

        // Get all bank accounts
        ResponseEntity<String> getAllResponse2 = restTemplate.exchange(
                BASE_URL + "/bankAccounts",
                HttpMethod.GET,
                null,
                String.class);

        System.out.println("All Bank Accounts: " + getAllResponse2.getBody());


        ResponseEntity<String> depositResponse = restTemplate.postForEntity(
                BASE_URL + "/bankAccounts/{id}/deposit?amount={amount}",
                null,
                String.class,
                accountId,
                depositAmount);

        System.out.println("Deposit Result: " + depositResponse.getBody());

        ResponseEntity<String> depositByNameResponse = restTemplate.postForEntity(
                BASE_URL + "/bankAccounts/{name}/deposit?amount={amount}",
                null,
                String.class,
                name,
                depositAmount);

        System.out.println("Deposit by name Result: " + depositByNameResponse.getBody());

        // Withdraw from a bank account
        long accountId2 = 2; // Change the account ID accordingly
        double withdrawAmount = 30.0; // Change the withdrawal amount accordingly

        ResponseEntity<String> withdrawResponse = restTemplate.postForEntity(
                BASE_URL + "/bankAccounts/{id}/withdraw?amount={amount}",
                null,
                String.class,
                accountId2,
                withdrawAmount);

        System.out.println("Withdraw Result: " + withdrawResponse.getBody());


        // name to find
        String nameToFind = "Nigel";

        try {
            // Make a GET request to find a single bank account by name
            ResponseEntity<BankAccount> findByNameResponse = restTemplate.getForEntity(
                    BASE_URL + "/bankAccounts/{name}",
                    BankAccount.class,
                    nameToFind);

            // Get the result as a single BankAccount
            BankAccount accountWithName = findByNameResponse.getBody();

            // Print the result
            System.out.println("Bank account with name '" + nameToFind + "': " + accountWithName);
        } catch (HttpClientErrorException.NotFound notFoundException) {
            // Handle 404 error (Resource not found)
            System.out.println("No bank account found with name '" + nameToFind + "'");
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
        }
    }
}