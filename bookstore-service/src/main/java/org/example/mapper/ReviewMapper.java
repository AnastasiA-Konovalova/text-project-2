package org.example.mapper;

import lombok.RequiredArgsConstructor;
import org.example.model.BookReview;
import org.example.model.ReviewEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

    private final BookMapper bookMapper;

    public BookReview toDto(ReviewEntity review) {
        BookReview dto = new BookReview();
        dto.setId(review.getId());
        dto.setBook(bookMapper.toDto(review.getBook()));
        dto.setReviewerId(review.getReviewer().getId());
        dto.setRating(review.getRating());
        dto.setText(review.getText());
        if (review.getCreatedAt() == null) dto.setCreatedAt(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        if (review.getUpdatedAt() == null) dto.setUpdatedAt(LocalDateTime.now().atOffset(ZoneOffset.UTC));

        return dto;
    }
}