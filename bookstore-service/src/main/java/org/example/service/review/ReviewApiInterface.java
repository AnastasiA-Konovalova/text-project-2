package org.example.service.review;

import org.example.model.BookReview;
import org.example.model.BookReviewInput;
import org.example.model.ChangeReviewRequest;
import org.example.model.Order;
import org.example.model.SortReview;

import java.util.List;

public interface ReviewApiInterface {

    BookReview changeReview(Integer reviewId, ChangeReviewRequest changeReviewRequest);

    void deleteReviewById(Integer reviewId);

    BookReview getBookReviewById(Integer reviewId);

    List<BookReview> getReviews(Integer bookId, SortReview sortReview, Order order, Integer limit, Integer offset);

    BookReview postReview(BookReviewInput bookReviewInput);
}