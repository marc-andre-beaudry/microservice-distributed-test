package com.marc.microservice.provisioning.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marc.microservice.provisioning.entity.Provision;
import com.marc.microservice.provisioning.repository.ProvisionRepository;
import com.marc.microservice.provisioning.ws.ProvisioningWS;

@Slf4j
@RestController
@RequestMapping("/api")
public class ProvisionController {

	@Value("${provisioning.event.queue.name}")
	private String provisioningEventQueueName;

	private final ObjectMapper mapper = new ObjectMapper();
	private final ProvisionRepository provisionRepository;
	private final RabbitTemplate rabbitTemplate;

	@Autowired
	public ProvisionController(ProvisionRepository provisionRepository, RabbitTemplate rabbitTemplate) {
		this.provisionRepository = provisionRepository;
		this.rabbitTemplate = rabbitTemplate;
	}

	@GetMapping("/provisions")
	@Transactional(readOnly = true)
	public ResponseEntity<?> getProvisions() {
		List<Provision> provisions = provisionRepository.findAll();
		List<ProvisioningWS> provisioningWSS = provisions.stream().map(provision -> ProvisioningWS.fromEntity(provision)).collect(Collectors.toList());
		return ResponseEntity.ok(provisioningWSS);
	}

	@GetMapping("/provisions/{uuid}")
	@Transactional(readOnly = true)
	public ResponseEntity<?> getProvision(@PathVariable String uuid) {
		Provision provision = provisionRepository.findByUuid(uuid);
		if (provision == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(ProvisioningWS.fromEntity(provision));
	}

	@PostMapping("/provisions")
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> createProvision(@RequestBody ProvisioningWS provisioningWS) {
		log.info("POST /provisions");

		Provision provision = new Provision();
		provision.setUuid(UUID.randomUUID().toString());
		provision.setCartUuid(provisioningWS.getCartUuid());
		provisionRepository.save(provision);

		try {
			rabbitTemplate.convertAndSend(provisioningEventQueueName, mapper.writeValueAsString(provision));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException();
		}

		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}
