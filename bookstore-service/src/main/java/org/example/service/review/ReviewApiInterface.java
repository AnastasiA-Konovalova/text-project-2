package org.example.service.review;

import org.example.model.BookReview;
import org.example.model.BookReviewInput;
import org.example.model.ChangeReviewRequest;

import java.util.List;

public interface ReviewApiInterface {

    BookReview changeReview(Integer reviewId, ChangeReviewRequest changeReviewRequest);

    void deleteReviewById(Integer reviewId);

    BookReview getBookReviewById(Integer bookReviewId);

    List<BookReview> getReviews(Integer bookId, Integer limit, Integer offset, String sortBook, String order);

    BookReview postReview(BookReviewInput bookReviewInput);
}