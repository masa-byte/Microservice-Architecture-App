package com.walcart.productmicroservice.repositories;
import com.walcart.productmicroservice.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
