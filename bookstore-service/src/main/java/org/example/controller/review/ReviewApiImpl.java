package org.example.controller.review;

import org.example.api.ReviewApi;
import org.example.model.BookReview;
import org.example.model.ChangeReviewRequest;
import org.example.model.PostReviewRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewApiImpl implements ReviewApi {

    @Override
    public ResponseEntity<BookReview> changeReview(Integer reviewId, ChangeReviewRequest changeReviewRequest) {
        BookReview bookReview = new BookReview();
        return ResponseEntity.ok(bookReview);
    }

    @Override
    public ResponseEntity<Void> deleteReviewById(Integer reviewId) {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<BookReview> getBookReviewById(Integer bookReviewId) {
        BookReview bookReview = new BookReview();
        return ResponseEntity.ok(bookReview);
    }

    @Override
    public ResponseEntity<List<BookReview>> getReviews(Integer limit, Integer offset, String sort, String order) {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<BookReview> postReview(Integer bookId, PostReviewRequest postReviewRequest) {
        BookReview bookReview = new BookReview();
        return ResponseEntity.ok(bookReview);
    }
}
