package com.walcart.productmicroservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.walcart.productmicroservice.domain.dtos.*;
import com.walcart.productmicroservice.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.walcart.productmicroservice.domain.entities.Review;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private Connection connection = null;
    private Channel reviewChannel = null;
    private final static String REVIEWS_QUEUE_NAME = "reviews";
    private final String consumerName;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) throws IOException, TimeoutException {
        this.consumerName = "product-microservice"; // + id for replication of microservice
        this.reviewRepository = reviewRepository;
        this.objectMapper = new ObjectMapper();
        this.configureConnection();
        this.createReviewMessageBroker();
    }

    private void configureConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        this.connection = factory.newConnection();
        this.reviewChannel = connection.createChannel();
        this.reviewChannel.queueDeclare(REVIEWS_QUEUE_NAME, true, false, false, null);
    }

    public Review createReview(ReviewDTO reviewDTO) {
        return reviewRepository.save(new Review(
                reviewDTO.getTitle(),
                reviewDTO.getDescription(),
                reviewDTO.getRating(),
                reviewDTO.getCustomerId(),
                ProductDTO.mapToProductID(reviewDTO.getProductDTO())));
    }

    private void createReviewMessageBroker() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            ReviewDTO reviewDTO = objectMapper.readValue(message, ReviewDTO.class);
            createReview(reviewDTO);
        };
        reviewChannel.basicConsume(REVIEWS_QUEUE_NAME, true, deliverCallback, consumerTag -> {});
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
