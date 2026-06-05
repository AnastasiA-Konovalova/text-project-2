package org.example.service.review;

import lombok.RequiredArgsConstructor;
import org.example.database.*;
import org.example.exception.NotFoundException;
import org.example.exception.UserDoesNotOwnDataException;
import org.example.mapper.ReviewMapper;
import org.example.mapper.SortReviewMapper;
import org.example.model.*;
import org.example.model.BookReview;
import org.example.model.BookReviewInput;
import org.example.model.ChangeReviewRequest;
import org.example.model.Order;
import org.example.model.SortReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewApiService implements ReviewApiInterface {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;


    @Override
    public BookReview changeReview(Integer reviewId, ChangeReviewRequest changeReviewRequest) {
        String email = authenticationReview();

        ReviewEntity review = existReviewEntity(reviewId);
        checkOwner(review, email);

        if (changeReviewRequest.getRating() != null) review.setRating(changeReviewRequest.getRating());
        if (changeReviewRequest.getText() != null) review.setText(changeReviewRequest.getText());
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    @Override
    public void deleteReviewById(Integer reviewId) {
        String email = authenticationReview();

        ReviewEntity review = existReviewEntity(reviewId);
        checkOwner(review, email);

        reviewRepository.deleteById(reviewId);
    }

    @Override
    public BookReview getBookReviewById(Integer reviewId) {
        String email = authenticationReview();

        ReviewEntity review = existReviewEntity(reviewId);
        checkOwner(review, email);

        return reviewMapper.toDto(review);
    }

    @Override
    public List<BookReview> getReviews(Integer bookId, SortReview sortReview, Order order, Integer limit, Integer offset) {
        existBookEntity(bookId);

        Sort sort = Sort.by("id");

        if (sortReview != null) {
            sort = Sort.by(SortReviewMapper.toField(sortReview));
        }
        if (order == Order.DESC) sort = sort.descending();

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
        String email = authenticationReview();

        BookEntity bookEntity = existBookEntity(bookReviewInput.getBookId());

        ReviewEntity review = new ReviewEntity();
        review.setBook(bookEntity);
        review.setText(bookReviewInput.getText());
        review.setRating(bookReviewInput.getRating());

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        review.setReviewer(user);

        reviewRepository.save(review);

        bookEntity.setReviewCount(bookEntity.getReviewCount() + 1);
        bookEntity.setAverageRating((bookEntity.getAverageRating() + bookReviewInput.getRating()) / bookEntity.getReviewCount());

        bookRepository.save(bookEntity);
        return reviewMapper.toDto(review);
    }

    private ReviewEntity existReviewEntity(Integer reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException("Review not found"));
    }

    private BookEntity existBookEntity(Integer bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found"));
    }

    private String authenticationReview() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private void checkOwner(ReviewEntity review, String email) {
        if (!review.getReviewer().getEmail().equals(email)) {
            throw new UserDoesNotOwnDataException("User doesn't data owner");
        }
    }
}