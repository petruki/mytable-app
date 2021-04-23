package com.github.petruki.mytable.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import com.github.petruki.mytable.model.Booking;
import com.github.petruki.mytable.model.Customer;
import com.github.petruki.mytable.model.Slots;
import com.github.petruki.mytable.model.Table;
import com.github.petruki.mytable.repository.TableRepository;

@Service
public class TableService {
	
	private final TableRepository tableRepository;
	
	@Autowired
	public TableService(TableRepository tableRepository) {
		this.tableRepository = tableRepository;
	}
	
    public List<Table> listTables() {
        return tableRepository.findAll();
    }
    
    public Table findTableById(String id) {
    	Optional<Table> table = tableRepository.findById(id);
    	
    	if (table.isPresent()) {
    		return table.get();
    	}
    	
    	throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    
    public Table createTable(@RequestBody Table table) {
    	table.setAvailableSlots(Slots.values().length);
        return tableRepository.save(table);
    }
    
    public Table updateTable(@PathVariable("id") String id, @RequestBody Table table) {
    	Optional<Table> tableFound = tableRepository.findById(id);
    	
    	if (!tableFound.isPresent()) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    	}
    	
    	Table update = tableFound.get();
    	update.setAlias(table.getAlias());
    	update.setAvailableSeats(table.getAvailableSeats());
    	
        return tableRepository.save(update);
    }
    
    public void deleteTable(@PathVariable("id") String id) {
    	tableRepository.deleteById(id);
    }

	public Table bookTable(String alias, Booking booking) {
		Table table = tableRepository.findByAlias(alias);
		validate(table);
		checkAvailability(table, booking);
		
		table.addBooking(booking);
		return tableRepository.save(table);
	}
	
	public Table cancelBooking(String alias, Customer customer) {
		Table table = tableRepository.findByAlias(alias);
		validate(table);
		
		table.getBookings().removeIf(c -> c.getCustomer().getPhone().equals(customer.getPhone()));
		table.setAvailableSlots(Slots.values().length - table.getBookings().size());
		return tableRepository.save(table);
	}
	
	private void validate(Table table) {
		if (table == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}
	
	private void checkAvailability(Table table, Booking booking) {
		if (table.getAvailableSeats() < booking.getNumPeople())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Number of available sits is incompatible");
		
		if (Slots.find(booking.getSlot()) == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
					"Invalid slot");
		
		for (Booking booked : table.getBookings()) {
			if (booked.getSlot().equals(booking.getSlot())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot already booked");
			}
		}
	}
}
