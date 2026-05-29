package org.example.service.book;

import org.example.model.BookEntity;
import org.example.model.Genre;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<BookEntity> hasId(Integer id) {
//            return ((root, query, criteriaBuilder) ->
//                    criteriaBuilder.equal(root.get("id"), id));
        return (root, query, criteriaBuilder) -> {
            if (id == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }

    public static Specification<BookEntity> hasAuthor(Integer authorId) {
        return (root, query, criteriaBuilder) -> {

            if (authorId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("author").get("id"), authorId);
        };
    }

    public static Specification<BookEntity> hasSeries(Integer seriesId) {
        return (root, query, criteriaBuilder) -> {
            if (seriesId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("series").get("id"), seriesId);
        };
    }

    public static Specification<BookEntity> hasPublisher(Integer publisherId) {


//        return ((root, query, criteriaBuilder) ->
         //       criteriaBuilder.equal(root.get("publisher").get("id"), publisherId));
        return (root, query, criteriaBuilder) -> {
            if (publisherId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("publisher").get("id"), publisherId);
        };
    }

    public static Specification<BookEntity> hasGenre(Genre genre) {
        return (root, query, criteriaBuilder) -> {
            if (genre == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("genre"), genre);
        };
    }

    public static Specification<BookEntity> hasPublisherName(String publisherName) {
        return (root, query, criteriaBuilder) -> {
            if (publisherName == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("publisher").get("name"), publisherName);
        };
    }

    public static Specification<BookEntity> hasPopular(Boolean isPopular) {
        return (root, query, criteriaBuilder) -> {
            if (isPopular == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isPopular"), isPopular);
        };
    }

    public static Specification<BookEntity> hasNew(Boolean isNew) {
        return (root, query, criteriaBuilder) -> {
            if (isNew == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isNew"), isNew);
        };
    }

}

