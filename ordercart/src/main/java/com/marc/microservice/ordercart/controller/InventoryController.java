package com.marc.microservice.ordercart.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.marc.microservice.ordercart.repository.ItemRepository;
import com.marc.microservice.ordercart.entity.Item;

@Slf4j
@RestController
@RequestMapping("/api")
public class InventoryController {

	private final ItemRepository itemRepository;

	@Autowired
	public InventoryController(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@RequestMapping(value = "/inventory/items", method = RequestMethod.GET)
	public List<Item> getItems() {
		return itemRepository.findAll();
	}
}
