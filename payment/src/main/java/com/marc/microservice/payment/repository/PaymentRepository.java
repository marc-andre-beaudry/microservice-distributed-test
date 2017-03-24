package com.marc.microservice.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marc.microservice.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Payment findByUuid(String uuid);
}
