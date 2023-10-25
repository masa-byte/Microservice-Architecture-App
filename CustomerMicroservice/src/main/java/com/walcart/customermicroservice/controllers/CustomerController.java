package com.walcart.customermicroservice.controllers;

import com.walcart.customermicroservice.domain.dtos.CustomerDTO;
import com.walcart.customermicroservice.domain.entities.Customer;
import com.walcart.customermicroservice.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping()
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        return new ResponseEntity<>(
                CustomerDTO.mapToCustomerDTO(customerService.createCustomer(customerDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable("id") long id) {
        return customerService.getCustomerById(id)
                .map(value -> new ResponseEntity<>(CustomerDTO.mapToCustomerDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerDTO> getCustomerByEmail(@PathVariable("email") String email) {
        return customerService.getCustomerByEmail(email)
                .map(value -> new ResponseEntity<>(CustomerDTO.mapToCustomerDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public ResponseEntity<Iterable<CustomerDTO>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        List<CustomerDTO> customerDTOs = new ArrayList<>();
        for (Customer customer : customers) {
            customerDTOs.add(CustomerDTO.mapToCustomerDTO(customer));
        }
        return new ResponseEntity<>(customerDTOs, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable("id") long id,@RequestBody CustomerDTO updatedCustomerDTO) {
        Customer updatedCustomer = customerService.updateCustomer(id, updatedCustomerDTO);
        if (updatedCustomer != null) {
            updatedCustomerDTO = CustomerDTO.mapToCustomerDTO(updatedCustomer);
            return new ResponseEntity<>(updatedCustomerDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") long id) {
        return customerService.deleteCustomer(id)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
