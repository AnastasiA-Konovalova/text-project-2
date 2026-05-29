package org.example.database;

import org.example.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookApiRepository extends JpaRepository<BookEntity, Integer>,
                                            JpaSpecificationExecutor<BookEntity> {
}
