package org.example.database;

import org.example.model.PublisherSeriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherSeriesRepository extends JpaRepository<PublisherSeriesEntity, Integer> {
}
