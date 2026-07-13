package org.example.database;

import org.example.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {
    Optional<PaymentEntity> findByOrderId(Integer orderId);

    Optional<PaymentEntity> findByPaymentOrderId(Integer paymentOrderId);

}
