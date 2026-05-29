package org.example.database;

import org.example.model.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewApiRepository extends JpaRepository<ReviewEntity, Integer> {

    Page<ReviewEntity> findByBookId(Integer bookId, PageRequest pageRequest);
}
