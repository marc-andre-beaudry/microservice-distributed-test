package com.marc.microservice.ordercart.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Table(name = "orderItem")
@Entity
@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, unique = true)
	private Long id;

	@Column(name = "uuid", updatable = false, unique = true)
	private String uuid = UUID.randomUUID().toString();

	@OneToOne(fetch= FetchType.EAGER)
	@JoinColumn(name = "item_id")
	private Item item;

	@Column(name = "quantity")
	private Integer quantity;

	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="cart_id")
	@JsonBackReference
	private OrderCart orderCart;
}
