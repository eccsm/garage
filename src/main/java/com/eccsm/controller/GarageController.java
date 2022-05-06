package com.eccsm.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.eccsm.model.Ticket;
import com.eccsm.service.GarageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GarageController {

	@Autowired
	GarageService service;

	@GetMapping("/status")
	public ResponseEntity<String> status() {
		try {
			String response = service.status();

			if (response == null)
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/park")
	public ResponseEntity<Object> park(@Valid @RequestBody Ticket ticket, BindingResult result, ModelMap model) {
		try {
			if (result.hasErrors()) {
				List<String> errors = result.getAllErrors().stream().map(e -> e.getDefaultMessage())
						.collect(Collectors.toList());
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
			Ticket response = service.park(ticket);

			String slotWord = " slot.";
			int slot = response.getVehicle().getAllocation();
			if (slot > 1)
				slotWord = " slots.";

			return new ResponseEntity<>("Allocated " + slot + slotWord, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/leave/{id}")
	public ResponseEntity<Object> leave(@PathVariable("id") long id) {
		try {
			service.leave(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

}
