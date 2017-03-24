package com.marc.microservice.ordercart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marc.microservice.ordercart.entity.OrderCart;

public interface OrderCartRepository extends JpaRepository<OrderCart, Long> {
	OrderCart findByUuid(String uuid);
}
