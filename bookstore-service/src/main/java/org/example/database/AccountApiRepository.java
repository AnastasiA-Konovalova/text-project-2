package org.example.database;

import org.example.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountApiRepository extends JpaRepository<AccountEntity, Integer> {
}
