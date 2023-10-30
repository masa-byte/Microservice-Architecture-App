package com.walcart.bff.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private String title;
    private String description;
    private Integer rating;
    private Long customerId;
    private ProductDTO productDTO;
}
