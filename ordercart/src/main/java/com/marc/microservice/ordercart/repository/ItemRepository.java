package com.marc.microservice.ordercart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marc.microservice.ordercart.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	Item findByUuid(String uuid);
}
