package com.marc.microservice.ordercart.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Table(name = "orderCart")
@Entity
@Data
public class OrderCart {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, unique = true)
	private Long id;

	@Column(name = "uuid", updatable = false, unique = true)
	private String uuid = UUID.randomUUID().toString();

	@OneToMany(mappedBy = "orderCart", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<OrderItem> items = new ArrayList<>();

	@Column(name = "status")
	private OrderStatus status = OrderStatus.NEW;
}
