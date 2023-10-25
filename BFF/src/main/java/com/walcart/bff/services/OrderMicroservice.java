package com.walcart.bff.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walcart.bff.domain.dtos.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static com.walcart.bff.Endpoints.ordersMicroserviceApiUrl;

@Service
public class OrderMicroservice {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderMicroservice() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

}
