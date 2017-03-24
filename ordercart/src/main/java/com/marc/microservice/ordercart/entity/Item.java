package com.marc.microservice.ordercart.entity;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name = "item")
@Entity
@Data
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, unique = true)
	private Long id;

	@Column(name = "uuid", updatable = false, unique = true)
	private String uuid = UUID.randomUUID().toString();

	@Column(name = "unit_price")
	private BigDecimal unitPrice;

	@Column(name = "description")
	private String description;
}
