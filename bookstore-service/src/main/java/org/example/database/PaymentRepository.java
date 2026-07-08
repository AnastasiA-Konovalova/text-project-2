package org.example.database;

import org.example.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {
    PaymentEntity findByOrderId(Integer orderId);

}
