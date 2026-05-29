package org.example.controller.review;

import lombok.RequiredArgsConstructor;
import org.example.api.ReviewApi;
import org.example.model.BookReview;
import org.example.model.BookReviewInput;
import org.example.model.ChangeReviewRequest;
import org.example.service.review.ReviewApiInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewApiImpl implements ReviewApi {

    private final ReviewApiInterface reviewInterface;

    @Override
    public ResponseEntity<BookReview> changeReview(Integer reviewId, ChangeReviewRequest changeReviewRequest) {
        return ResponseEntity.ok(reviewInterface.changeReview(reviewId, changeReviewRequest));
    }

    @Override
    public ResponseEntity<Void> deleteReviewById(Integer reviewId) {
        reviewInterface.deleteReviewById(reviewId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<BookReview> getBookReviewById(Integer bookReviewId) {
        return ResponseEntity.ok(reviewInterface.getBookReviewById(bookReviewId));
    }

    @Override
    public ResponseEntity<List<BookReview>> getReviews(Integer bookId, Integer limit, Integer offset, String sortBook, String order) {
        return ResponseEntity.ok(reviewInterface.getReviews(bookId,limit, offset, sortBook, order));
    }

    @Override
    public ResponseEntity<BookReview> postReview(BookReviewInput bookReviewInput) {
        return ResponseEntity.ok(reviewInterface.postReview(bookReviewInput));
    }
}
