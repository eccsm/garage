package com.eccsm.model;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "tickets")
public class Ticket {

	public enum Vehicle {
		CAR(1),
		JEEP(2),
		TRUCK(4);

		Integer allocation;

		Vehicle(Integer allocation) {

			this.allocation = allocation;
		}

		public Integer getAllocation() {
			return allocation;
		}

	}

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "plate")
	@NotBlank(message = "{validation.plate.notBlank}")
	private String plate;

	@Column(name = "color")
	@NotBlank(message = "{validation.color.notBlank}")
	@Size(min = 2, max = 15, message = "{validation.color.constraints}")
	@Pattern(regexp = "^[A-Za-z]*$", message="{validation.color.ambiguous}")
	private String color;

	@Column(name = "vehicle")
	@NotNull(message = "{validation.vehicle.notNull}")
	private Vehicle vehicle;

	public Ticket() {
	}

	public Ticket(long id, String plate, String color, Vehicle vehicle) {
		this.id = id;
		this.plate = plate;
		this.color = color;
		this.vehicle = vehicle;
	}

	public long getId() {
		return id;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	@Override
	public String toString() {
		return plate + " " + color + " " + vehicle;
	}

}
