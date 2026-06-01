package org.example.database;

import org.example.model.BasketDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketDetailRepository extends JpaRepository<BasketDetailEntity, Integer> {
}
