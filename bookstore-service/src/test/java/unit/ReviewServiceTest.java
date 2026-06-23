package unit;

import org.example.database.BookRepository;
import org.example.database.ReviewRepository;
import org.example.database.UserRepository;
import org.example.mapper.ReviewMapper;
import org.example.model.*;
import org.example.model.BookReview;
import org.example.model.BookReviewInput;
import org.example.model.ChangeReviewRequest;
import org.example.model.Order;
import org.example.model.SortReview;
import org.example.service.review.ReviewApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewApiService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Test
    void getBookReviewById_shouldReturnBookReviewDto() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Integer reviewId = 1;

        ReviewEntity entity = new ReviewEntity();
        UserEntity userEntity = new UserEntity();
        entity.setId(reviewId);
        entity.setReviewer(userEntity);
        entity.getReviewer().setEmail("testUser");

        BookReview dto = new BookReview();
        dto.setId(reviewId);

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(entity));
        when(reviewMapper.toDto(entity))
                .thenReturn(dto);

        BookReview result = reviewService.getBookReviewById(reviewId);

        assertNotNull(result);
        assertEquals(reviewId, result.getId());

        verify(reviewRepository).findById(reviewId);
        verify(reviewMapper).toDto(entity);
    }

    @Test
    void getReviews_shouldReturnListBookReviewDto() {
        Integer bookId = 1;

        ReviewEntity reviewOne = new ReviewEntity();
        ReviewEntity reviewTwo = new ReviewEntity();

        BookReview dtoOne = new BookReview();
        BookReview dtoTwo = new BookReview();

        List<ReviewEntity> entities = List.of(reviewOne, reviewTwo);

        Page<ReviewEntity> page = new PageImpl<>(entities);

        BookEntity bookEntity = new BookEntity();
        bookEntity.setId(bookId);
        bookEntity.setReviewCount(1);
        bookEntity.setAverageRating(3);

        when(reviewMapper.toDto(reviewOne)).thenReturn(dtoOne);
        when(reviewMapper.toDto(reviewTwo)).thenReturn(dtoTwo);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
        when(reviewRepository.findByBookId(
                eq(bookId),
                any(PageRequest.class)
        )).thenReturn(page);

        List<BookReview> result = reviewService.getReviews(bookEntity.getId(), SortReview.REVIEWER_ID, Order.ASC, 10, 0);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dtoOne, result.get(0));
        assertEquals(dtoTwo, result.get(1));

        verify(reviewRepository).findByBookId(
                eq(bookId),
                any(PageRequest.class)
        );
        verify(reviewMapper).toDto(reviewOne);
        verify(reviewMapper).toDto(reviewTwo);
    }

    @Test
    void patchChangeReview_shouldReturnBookReviewDto() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Integer reviewId = 1;

        UserEntity userEntity = new UserEntity();
        ReviewEntity entity = new ReviewEntity();
        entity.setId(reviewId);
        entity.setReviewer(userEntity);
        entity.getReviewer().setEmail("testUser");

        ChangeReviewRequest changeReviewRequest = new ChangeReviewRequest();
        changeReviewRequest.setText("Updated review");

        BookReview dto = new BookReview();
        dto.setId(reviewId);

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(entity));
        when(reviewMapper.toDto(entity))
                .thenReturn(dto);
        when(reviewRepository.save(any()))
                .thenReturn(entity);

        BookReview result = reviewService.changeReview(reviewId, changeReviewRequest);

        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals("Updated review", changeReviewRequest.getText());

        verify(reviewRepository).findById(reviewId);
        verify(reviewMapper).toDto(entity);
        verify(reviewRepository).save(entity);
    }

    @Test
    void postReviewReview_shouldReturnBookReviewDto() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Integer reviewId = 1;
        Integer bookId = 1;
        String email = "testUser";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        BookEntity bookEntity = new BookEntity();
        bookEntity.setId(bookId);
        bookEntity.setReviewCount(1);
        bookEntity.setAverageRating(3);

        ReviewEntity entity = new ReviewEntity();
        entity.setId(reviewId);
        entity.setReviewer(userEntity);
        entity.getReviewer().setEmail("testUser");

        BookReviewInput bookReviewInput = new BookReviewInput();
        bookReviewInput.setText("New review");
        bookReviewInput.setBookId(bookId);

        BookReview dto = new BookReview();
        dto.setId(reviewId);

        when(reviewMapper.toDto(any(ReviewEntity.class)))
                .thenReturn(dto);
        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(bookEntity));
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(userEntity));
        when(reviewRepository.save(any()))
                .thenReturn(entity);

        BookReview result = reviewService.postReview(bookReviewInput);

        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals("New review", bookReviewInput.getText());

        verify(userRepository).findByEmail(email);
        verify(bookRepository).findById(bookId);
        verify(reviewRepository).save(any(ReviewEntity.class));
        verify(reviewMapper).toDto(any(ReviewEntity.class));

    }

    @Test
    void deleteReviewById_shouldReturnNull() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Integer reviewId = 1;
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("testUser");

        ReviewEntity entity = new ReviewEntity();
        entity.setId(reviewId);
        entity.setReviewer(userEntity);

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(entity));

        reviewService.deleteReviewById(reviewId);

        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).deleteById(entity.getId());
    }
}