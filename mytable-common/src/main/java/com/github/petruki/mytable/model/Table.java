package com.github.petruki.mytable.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Document(collection = "tables")
public class Table {
	
	@Id
	private String id;
	
	@Indexed(unique = true)
	private String alias;
	
	private int availableSeats;
	
	private int availableSlots;
	
	private List<Booking> bookings;
	
	public Table() {}
	
	public Table(String alias, int availableSeats) {
		this.alias = alias;
		this.availableSeats = availableSeats;
		this.availableSlots = Slots.values().length;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(int availableSits) {
		this.availableSeats = availableSits;
	}

	public int getAvailableSlots() {
		return availableSlots;
	}

	public void setAvailableSlots(int availableSlots) {
		this.availableSlots = availableSlots;
	}

	public List<Booking> getBookings() {
		if (bookings == null)
			bookings = new ArrayList<>();
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
	
	public void addBooking(Booking booking) {
		if (bookings == null)
			bookings = new ArrayList<>();
		
		this.bookings.add(booking);
		this.availableSlots--;
	}
	
	public void removeBooking(Booking booking) {
		bookings.remove(booking);
		this.availableSlots++;
	}

}
