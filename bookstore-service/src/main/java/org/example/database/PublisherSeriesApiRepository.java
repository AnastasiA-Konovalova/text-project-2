package org.example.database;

import org.example.model.PublisherSeriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherSeriesApiRepository extends JpaRepository<PublisherSeriesEntity, Integer> {
}
