package com.walcart.bff.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walcart.bff.domain.dtos.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static com.walcart.bff.Endpoints.customersMicroserviceApiUrl;

@Service
public class CustomerMicroservice {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String customersApiUrl = customersMicroserviceApiUrl + "/customers";
    @Autowired
    public CustomerMicroservice() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) throws IOException, InterruptedException {
        String url = customersApiUrl;
        URI uri = URI.create(url);
        String requestBody = objectMapper.writeValueAsString(customerDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 201) {
            String responseBody = response.body();
            CustomerDTO createdCustomerDTO = objectMapper.readValue(responseBody, CustomerDTO.class);
            return createdCustomerDTO;
        } else {
            return null;
        }
    }

    public Optional<CustomerDTO> getCustomerByEmail(String email) throws IOException, InterruptedException {
        String url = customersApiUrl + "/email/" + email;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            CustomerDTO customerDTO = objectMapper.readValue(responseBody, CustomerDTO.class);
            return Optional.of(customerDTO);
        } else {
            return Optional.empty();
        }
    }

    public Optional<CustomerDTO> getCustomerById(long id) throws IOException, InterruptedException {
        String url = customersApiUrl + "/" + id;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            CustomerDTO customerDTO = objectMapper.readValue(responseBody, CustomerDTO.class);
            return Optional.of(customerDTO);
        } else {
            return Optional.empty();
        }
    }

    public List<CustomerDTO> getAllCustomers() throws IOException, InterruptedException {
        String url = customersApiUrl;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            CustomerDTO[] customerDTOArray = objectMapper.readValue(responseBody, CustomerDTO[].class);
            return List.of(customerDTOArray);
        } else {
            return null;
        }
    }

    public Optional<CustomerDTO> updateCustomer(long id, CustomerDTO updatedCustomerDTO) throws IOException, InterruptedException {
        String url = customersApiUrl + "/" + id;
        URI uri = URI.create(url);
        String requestBody = objectMapper.writeValueAsString(updatedCustomerDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            CustomerDTO customerDTO = objectMapper.readValue(responseBody, CustomerDTO.class);
            return Optional.of(customerDTO);
        } else {
            return null;
        }
    }

    public boolean deleteCustomer(long id) throws IOException, InterruptedException {
        String url = customersApiUrl + "/" + id;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        return statusCode == 200;
    }
}
