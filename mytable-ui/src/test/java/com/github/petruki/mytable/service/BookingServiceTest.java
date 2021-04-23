package com.github.petruki.mytable.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.petruki.mytable.model.Booking;
import com.github.petruki.mytable.model.Customer;
import com.github.petruki.mytable.model.Slots;
import com.github.petruki.mytable.model.Table;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
public class BookingServiceTest {
	
	private static BookingService bookingService;
	
	private static MockWebServer mockBackEnd;
	
	private MockResponse generateBookTableResponseMock(
			Table table, Booking booking, int statusCode) {
		
		final String response = ""
				+ "{"
				+ "    \"id\": \"605d717615a26e2814cdb1af\","
				+ "    \"alias\": \""+ table.getAlias() +"\","
				+ "    \"availableSeats\": "+ table.getAvailableSeats() +","
				+ "    \"availableSlots\": "+ table.getAvailableSlots() +","
				+ "    \"bookings\": ["
				+ "        {"
				+ "            \"customer\": {"
				+ "                \"name\": \""+ booking.getCustomer().getName() +"\","
				+ "                \"phone\": \""+ booking.getCustomer().getPhone() +"\""
				+ "            },"
				+ "            \"slot\": \""+ booking.getSlot() +"\","
				+ "            \"numPeople\": "+ booking.getNumPeople()
				+ "        }"
				+ "    ]"
				+ "}";
			
		return new MockResponse()
			.setResponseCode(statusCode)
			.setBody(response)
			.addHeader("Content-Type", "application/json");
	}
	
	private MockResponse generateListTableResponseMock(
			String alias, int seats, int slots, int statusCode) {
		final String response = "[{"
				+ "    \"id\": \"605f84d0465f6a499b0b980c\","
				+ "    \"alias\": \""+ alias +"\","
				+ "    \"availableSeats\": "+ seats +","
				+ "    \"availableSlots\": "+ slots +","
				+ "    \"bookings\": []"
				+ "}]";
			
		return new MockResponse()
			.setResponseCode(statusCode)
			.setBody(response)
			.addHeader("Content-Type", "application/json");
	}
	
	private MockResponse generateTableResponseMock(
			String alias, int seats, int slots, int statusCode) {
		final String response = "{"
				+ "    \"id\": \"605f84d0465f6a499b0b980c\","
				+ "    \"alias\": \""+ alias +"\","
				+ "    \"availableSeats\": "+ seats +","
				+ "    \"availableSlots\": "+ slots +","
				+ "    \"bookings\": []"
				+ "}";
			
		return new MockResponse()
			.setResponseCode(statusCode)
			.setBody(response)
			.addHeader("Content-Type", "application/json");
	}
	
	@BeforeAll
	static void setup() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        
        System.setProperty("api.url", "http://localhost:" + mockBackEnd.getPort() + "/");  
        bookingService = new BookingService("http://localhost:" + mockBackEnd.getPort() + "/");
    }
	
	@Test
	void shouldListTable() {
		//given
		Table table = new Table("Table01", 5);
		
		//mock response
		mockBackEnd.enqueue(
				generateListTableResponseMock(
						table.getAlias(), 
						table.getAvailableSeats(), 
						Slots.values().length,
						200));
		
		//test
		List<Table> tableList = bookingService.listTables();
		assertEquals(1, tableList.size());
		assertEquals(table.getAlias(), tableList.get(0).getAlias());
	}
	
	@Test
	void shouldBookTable() {
		//given
		Table table = new Table("Table01", 5);
		Customer customer = new Customer("Roger", "7785120000");
		Booking booking = new Booking(customer, 3, Slots.S08_00.toString());
		table.addBooking(booking);
		
		//mock response
		mockBackEnd.enqueue(
				generateBookTableResponseMock(table, booking, 201));
		
		//test
		Table tableBooked = bookingService.bookTable(table.getAlias(), booking);
		assertEquals(table.getBookings(), tableBooked.getBookings());
	}
	
	@Test
	void shouldCancelBooking() {
		//given
		Table table = new Table("Table01", 5);
		Customer customer = new Customer("Roger", "7785120000");
		
		//mock response
		mockBackEnd.enqueue(
				generateTableResponseMock(
						table.getAlias(), 
						table.getAvailableSeats(), 
						table.getAvailableSlots(), 
						201));
		
		//test
		Table tableCanceled = bookingService.cancelBooking(table.getAlias(), customer);
		assertEquals(table.getBookings(), tableCanceled.getBookings());
	}
}
