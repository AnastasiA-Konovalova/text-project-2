package org.example.database;

import org.example.model.Basket;
import org.example.model.BasketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<BasketEntity, Integer> {
}
