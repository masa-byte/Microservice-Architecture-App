package com.walcart.customermicroservice.services;

import com.walcart.customermicroservice.domain.dtos.CustomerDTO;
import com.walcart.customermicroservice.domain.entities.Customer;
import com.walcart.customermicroservice.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(CustomerDTO customerDTO) {
        return customerRepository.save(new Customer(
                customerDTO.getFirstName(),
                customerDTO.getLastName(),
                customerDTO.getEmail(),
                customerDTO.getEmail(),
                customerDTO.getTelephone()
        ));
    }

    public Optional<Customer> getCustomerById(long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(long id, CustomerDTO updatedCustomerDTO) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setFirstName(updatedCustomerDTO.getFirstName());
                    existingCustomer.setLastName(updatedCustomerDTO.getLastName());
                    existingCustomer.setEmail(updatedCustomerDTO.getEmail());
                    existingCustomer.setPassword(updatedCustomerDTO.getPassword());
                    existingCustomer.setTelephone(updatedCustomerDTO.getTelephone());
                    return customerRepository.save(existingCustomer);
                })
                .orElse(null);
    }

    public boolean deleteCustomer(long id) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    customerRepository.delete(existingCustomer);
                    return true;
                })
                .orElse(false);
    }
}
