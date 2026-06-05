package org.example.mapper;

import org.example.model.SortReview;

public class SortReviewMapper {

    public static String toField(SortReview sortReview) {
        return switch (sortReview) {
            case ID -> "id";
            case REVIEWER_ID -> "reviewerId";
            case RATING -> "rating";
        };
    }
}
