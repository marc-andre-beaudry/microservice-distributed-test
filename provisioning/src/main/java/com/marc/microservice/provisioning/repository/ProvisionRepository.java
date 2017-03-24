package com.marc.microservice.provisioning.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marc.microservice.provisioning.entity.Provision;

public interface ProvisionRepository extends JpaRepository<Provision, Long> {
	Provision findByUuid(String uuid);
}
