package org.example.database;

import org.example.model.BasketDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasketDetailRepository extends JpaRepository<BasketDetailEntity, Integer> {
    Optional<BasketDetailEntity> findByBasketIdAndBookId(Integer basketId, Integer bookId);
}
