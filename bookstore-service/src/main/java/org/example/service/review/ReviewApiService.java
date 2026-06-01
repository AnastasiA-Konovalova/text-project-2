package org.example.service.review;

import lombok.RequiredArgsConstructor;
import org.example.database.BookApiRepository;
import org.example.database.ReviewApiRepository;
import org.example.exeception.ClassNotFoundException;
import org.example.mapper.ReviewMapper;
import org.example.model.*;
import org.example.model.BookReview;
import org.example.model.BookReviewInput;
import org.example.model.ChangeReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewApiService implements ReviewApiInterface {

    private final ReviewApiRepository reviewRepository;
    private final BookApiRepository bookApiRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public BookReview changeReview(Integer reviewId, ChangeReviewRequest changeReviewRequest) {
        ReviewEntity reviewEntity = existReviewEntity(reviewId);

        if (changeReviewRequest.getRating() != null) reviewEntity.setRating(changeReviewRequest.getRating());
        if (changeReviewRequest.getText() != null) reviewEntity.setText(changeReviewRequest.getText());
        reviewEntity.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(reviewEntity);
        return reviewMapper.toDto(reviewEntity);
    }

    @Override
    public void deleteReviewById(Integer reviewId) {
        existReviewEntity(reviewId);
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public BookReview getBookReviewById(Integer bookReviewId) {
        ReviewEntity reviewEntity = existReviewEntity(bookReviewId);
        return reviewMapper.toDto(reviewEntity);
    }

    @Override
    public List<BookReview> getReviews(Integer bookId, Integer limit, Integer offset, String sortBook, String order) {
        existBookEntity(bookId);

        Sort sort;
        if ("reviewerId".equalsIgnoreCase(sortBook)) sort = Sort.by("reviewerId");
        else if ("rating".equalsIgnoreCase(sortBook)) sort = Sort.by("rating");
        else sort = Sort.by("id");

        if ("desc".equalsIgnoreCase(order)) sort = sort.descending();

        PageRequest pageRequest = PageRequest.of(
                offset / limit,
                limit,
                sort
        );

        Page<ReviewEntity> page = reviewRepository.findByBookId(bookId, pageRequest);

        return page.getContent()
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public BookReview postReview(BookReviewInput bookReviewInput) {
        BookEntity bookEntity = existBookEntity(bookReviewInput.getBookId());

        ReviewEntity review = new ReviewEntity();
        review.setBook(bookEntity);
        review.setText(bookReviewInput.getText());
        review.setRating(bookReviewInput.getRating());
        review.setReviewerId(bookReviewInput.getReviewerId());

        reviewRepository.save(review);
        bookEntity.setReviewCount(bookEntity.getReviewCount() + 1);

        bookApiRepository.save(bookEntity);
        return reviewMapper.toDto(review);
    }

    private ReviewEntity existReviewEntity(Integer reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new ClassNotFoundException("Review not found"));
    }

    private BookEntity existBookEntity(Integer bookId) {
        return bookApiRepository.findById(bookId).orElseThrow(() -> new ClassNotFoundException("Book not found"));
    }

}