package com.walcart.productmicroservice.domain.dtos;

import com.walcart.productmicroservice.domain.entities.Review;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private String title;
    private String description;
    private Integer rating;
    private ProductDTO productDTO;

    public static ReviewDTO mapToReviewDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getTitle(),
                review.getDescription(),
                review.getRating(),
                ProductDTO.mapToProductDTO(review.getProduct()));
    }
}
