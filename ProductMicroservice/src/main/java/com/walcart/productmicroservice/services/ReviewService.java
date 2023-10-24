package com.walcart.productmicroservice.services;

import com.walcart.productmicroservice.domain.dtos.*;
import com.walcart.productmicroservice.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.walcart.productmicroservice.domain.entities.Review;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review createReview(ReviewDTO reviewDTO) {
        return reviewRepository.save(new Review(
                reviewDTO.getTitle(),
                reviewDTO.getDescription(),
                reviewDTO.getRating(),
                ProductDTO.mapToProduct(reviewDTO.getProductDTO())));
    }

    public Optional<Review> getReviewById(long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // update not needed since reviews can't be updated

    public boolean deleteReview(long id) {
        return reviewRepository.findById(id)
                .map(existingReview -> {
                    reviewRepository.delete(existingReview);
                    return true;
                })
                .orElse(false);
    }
}
