package com.walcart.productmicroservice.repositories;
import com.walcart.productmicroservice.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>{
}
