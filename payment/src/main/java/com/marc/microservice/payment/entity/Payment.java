package com.marc.microservice.payment.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, unique = true, updatable = false)
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private String uuid;

	@Column(nullable = false, updatable = false)
	private String cartUuid;

	@Column(nullable = false, updatable = false)
	private BigDecimal amount;
}
