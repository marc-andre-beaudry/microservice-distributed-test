package com.marc.microservice.provisioning.entity;

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
@Table(name = "provisioning")
public class Provision {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, unique = true, updatable = false)
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private String uuid;

	@Column(nullable = false, updatable = false)
	private String cartUuid;
}
