package com.walcart.productmicroservice.controllers;

import com.walcart.productmicroservice.domain.dtos.ReviewDTO;
import com.walcart.productmicroservice.domain.entities.Review;
import com.walcart.productmicroservice.services.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping()
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        return new ResponseEntity<>(
                ReviewDTO.mapToReviewDTO(reviewService.createReview(reviewDTO)),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable("id") long id) {
        return reviewService.getReviewById(id)
                .map(value -> new ResponseEntity<>(ReviewDTO.mapToReviewDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public ResponseEntity<Iterable<ReviewDTO>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        List<ReviewDTO> reviewsDTOs = new ArrayList<>();
        for (Review review : reviews) {
            reviewsDTOs.add(ReviewDTO.mapToReviewDTO(review));
        }
        return new ResponseEntity<>(reviewsDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteReview(@PathVariable("id") long id) {
        return reviewService.deleteReview(id)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
