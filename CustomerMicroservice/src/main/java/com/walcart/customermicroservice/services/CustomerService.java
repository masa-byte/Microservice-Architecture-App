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
                    if(updatedCustomerDTO.getFirstName() != null)
                        existingCustomer.setFirstName(updatedCustomerDTO.getFirstName());
                    if(updatedCustomerDTO.getLastName() != null)
                        existingCustomer.setLastName(updatedCustomerDTO.getLastName());
                    if(updatedCustomerDTO.getEmail() != null)
                        existingCustomer.setEmail(updatedCustomerDTO.getEmail());
                    if(updatedCustomerDTO.getPassword() != null)
                        existingCustomer.setPassword(updatedCustomerDTO.getPassword());
                    if(updatedCustomerDTO.getTelephone() != null)
                        existingCustomer.setTelephone(updatedCustomerDTO.getTelephone());
                    if(updatedCustomerDTO.getEnabled() != null)
                        existingCustomer.setEnabled(updatedCustomerDTO.getEnabled());
                    return customerRepository.save(existingCustomer);
                })
                .orElse(null);
    }

    public boolean deleteCustomer(long id) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setEnabled(false);
                    customerRepository.save(existingCustomer);
                    return true;
                })
                .orElse(false);
    }
}
