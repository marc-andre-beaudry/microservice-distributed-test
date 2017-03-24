package com.marc.microservice.payment.controller;

import java.math.BigDecimal;
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
import com.marc.microservice.payment.ws.PaymentBean;
import com.marc.microservice.payment.entity.Payment;
import com.marc.microservice.payment.repository.PaymentRepository;

@Slf4j
@RestController
@RequestMapping("/api")
public class PaymentController {

	private static final BigDecimal MAX_ALLOWED_AMOUNT = BigDecimal.valueOf(1000);

	@Value("${payment.event.queue.name}")
	private String paymentEventQueueName;

	private final ObjectMapper mapper = new ObjectMapper();
	private final PaymentRepository paymentRepository;
	private final RabbitTemplate rabbitTemplate;

	@Autowired
	public PaymentController(PaymentRepository paymentRepository, RabbitTemplate rabbitTemplate) {
		this.paymentRepository = paymentRepository;
		this.rabbitTemplate = rabbitTemplate;
	}

	@GetMapping("/payments")
	@Transactional(readOnly = true)
	public ResponseEntity<?> getPayments() {
		List<Payment> payments = paymentRepository.findAll();
		List<PaymentBean> paymentBeans = payments.stream().map(payment -> PaymentBean.fromEntity(payment)).collect(Collectors.toList());
		return ResponseEntity.ok(paymentBeans);
	}

	@GetMapping("/payments/{uuid}")
	@Transactional(readOnly = true)
	public ResponseEntity<?> getPayment(@PathVariable String uuid) {
		Payment payment = paymentRepository.findByUuid(uuid);
		if (payment == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(PaymentBean.fromEntity(payment));
	}

	@PostMapping("/payments")
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> createPayment(@RequestBody PaymentBean paymentBean) {
		log.info("POST /payments");

		// Simulating gateway limitations
		if (MAX_ALLOWED_AMOUNT.compareTo(paymentBean.getAmount()) < 0) {
			return ResponseEntity.badRequest().build();
		}

		Payment payment = new Payment();
		payment.setUuid(UUID.randomUUID().toString());
		payment.setCartUuid(paymentBean.getCartUuid());
		payment.setAmount(paymentBean.getAmount());
		paymentRepository.save(payment);

		try {
			rabbitTemplate.convertAndSend(paymentEventQueueName, mapper.writeValueAsString(payment));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException();
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}
