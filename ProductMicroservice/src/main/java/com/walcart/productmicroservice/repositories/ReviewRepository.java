package com.walcart.productmicroservice.repositories;
import com.walcart.productmicroservice.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>{
}
