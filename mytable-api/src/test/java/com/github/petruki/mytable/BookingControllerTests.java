package com.github.petruki.mytable;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.petruki.mytable.model.Booking;
import com.github.petruki.mytable.model.Customer;
import com.github.petruki.mytable.model.Slots;
import com.github.petruki.mytable.model.Table;
import com.github.petruki.mytable.service.TableService;
import com.google.gson.Gson;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureDataMongo
@AutoConfigureMockMvc
class BookingControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private TableService tableService;
	
	private final Gson GSON = new Gson();
	
	@Test
	void shouldReturnTables() throws Exception {
		//given
		Table table1 = tableService.createTable(new Table("Table01", 5));
		Table table2 = tableService.createTable(new Table("Table02", 4));
		
		Collection<Table> tables = new ArrayList<>();
		tables.add(table1);
		tables.add(table2);
		
		//test
		this.mockMvc.perform(get("/api/v1/booking/")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(table1.getAlias())))
			.andExpect(content().string(containsString(table2.getAlias())));
	}
	
	@Test
	void shouldBookTable() throws Exception {
		//given
		Table table1 = tableService.createTable(new Table("Table003", 5));
		
		Customer customer = new Customer("Roger", "7785120000");
		Booking booking = new Booking(customer, 3, Slots.S08_00.toString());
		String jsonRequest = GSON.toJson(booking);
		
		//test
		this.mockMvc.perform(post("/api/v1/booking/" + table1.getAlias())
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonRequest))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(table1.getAlias())))
			.andExpect(content().string(containsString(customer.getName())))
			.andExpect(content().string(containsString(customer.getPhone())));
	}
	
	@Test
	void shouldCancelBooking() throws Exception {
		//given
		Table table1 = tableService.createTable(new Table("Table04", 5));
		
		Customer customer = new Customer("John", "7785121111");
		Booking booking = new Booking(customer, 3, Slots.S08_00.toString());
		String jsonRequest = GSON.toJson(booking);
		
		//book
		this.mockMvc.perform(post("/api/v1/booking/" + table1.getAlias())
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonRequest))
			.andExpect(status().isOk());
		
		//test cancelation
		this.mockMvc.perform(post("/api/v1/booking/cancel/" + table1.getAlias())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldNotBookTable_exceedSeats() throws Exception {
		//given
		Table table1 = tableService.createTable(new Table("Table05", 5));
		
		Customer customer = new Customer("Mike", "7785122222");
		Booking booking = new Booking(customer, 6, Slots.S08_00.toString());
		String jsonRequest = GSON.toJson(booking);
		
		//test
		MvcResult result = this.mockMvc.perform(post("/api/v1/booking/" + table1.getAlias())
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonRequest))
			.andExpect(status().isBadRequest())
			.andReturn();
		
		String content = result.getResolvedException().getMessage();
		assertEquals("400 BAD_REQUEST \"Number of available sits is incompatible\"", content);
	}
	
	@Test
	void shouldNotBookTable_slotAlreadyBooked() throws Exception {
		//given
		//first customer
		Table table1 = tableService.createTable(new Table("Table06", 5));
		Customer customer1 = new Customer("Alan", "7785123333");
		Booking booking1 = new Booking(customer1, 3, Slots.S08_00.toString()); // booked for 8:00
		tableService.bookTable(table1.getAlias(), booking1);
		
		//given
		//second customer
		Customer customer2 = new Customer("Peter", "7785124444");
		Booking booking2 = new Booking(customer2, 4, Slots.S08_00.toString()); // booked for 8:00
		String jsonRequest = GSON.toJson(booking2);
		
		//test
		MvcResult result = this.mockMvc.perform(post("/api/v1/booking/" + table1.getAlias())
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonRequest))
			.andExpect(status().isBadRequest())
			.andReturn();
		
		String content = result.getResolvedException().getMessage();
		assertEquals("400 BAD_REQUEST \"Slot already booked\"", content);
	}
	
	@Test
	void shouldNotBookTable_tableDoesNotExist() throws Exception {
		//given
		Customer customer = new Customer("Roger", "7785120000");
		Booking booking = new Booking(customer, 3, Slots.S08_00.toString());
		String jsonRequest = GSON.toJson(booking);
		
		//test
		this.mockMvc.perform(post("/api/v1/booking/NOT_VALID_TABLE_ALIAS")
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonRequest))
			.andExpect(status().isNotFound());
	}
}
