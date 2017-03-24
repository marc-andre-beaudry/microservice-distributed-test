package com.marc.microservice.ordercart.config;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.marc.microservice.ordercart.repository.ItemRepository;
import com.marc.microservice.ordercart.entity.Item;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private ItemRepository itemRepository;

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		seedInventoryItems();
	}

	private void seedInventoryItems() {
		Item item1 = new Item();
		item1.setUuid("item1");
		item1.setUnitPrice(BigDecimal.valueOf(10));
		item1.setDescription("This is item 1");

		Item item2 = new Item();
		item2.setUuid("item2");
		item2.setUnitPrice(BigDecimal.valueOf(100));
		item2.setDescription("This is item 2");

		Item item3 = new Item();
		item3.setUuid("item3");
		item3.setUnitPrice(BigDecimal.valueOf(1000));
		item3.setDescription("This is item 3");

		itemRepository.save(Stream.of(item1, item2, item3).collect(Collectors.toList()));
	}
}
