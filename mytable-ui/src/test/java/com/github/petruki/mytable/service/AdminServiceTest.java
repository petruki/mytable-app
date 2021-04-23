package com.github.petruki.mytable.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.petruki.mytable.model.Slots;
import com.github.petruki.mytable.model.Table;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
public class AdminServiceTest {
	
	private static AdminService adminService;
	
	private static MockWebServer mockBackEnd;
	
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
	
	@BeforeAll
	static void setup() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        
        System.setProperty("api.url", "http://localhost:" + mockBackEnd.getPort() + "/");
        adminService = new AdminService("http://localhost:" + mockBackEnd.getPort() + "/");
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
		List<Table> tableList = adminService.listTables();
		assertEquals(1, tableList.size());
		assertEquals(table.getAlias(), tableList.get(0).getAlias());
	}
	
	@Test
	void shouldCreateTable() {
		//given
		Table table = new Table("Table01", 5);
		
		//mock response
		mockBackEnd.enqueue(
				generateTableResponseMock(
						table.getAlias(), 
						table.getAvailableSeats(), 
						Slots.values().length,
						201));
		
		//test
		Table created = adminService.createTable(table);
		assertEquals(table.getAlias(), created.getAlias());
		assertEquals(table.getAvailableSeats(), created.getAvailableSeats());
		assertEquals(table.getAvailableSlots(), created.getAvailableSlots());
	}
	
	@Test
	void shouldUpdateTable_from5SeatsTo10Seats() {
		//given
		Table table = new Table("Table01", 5);
		table.setId("605f84d0465f6a499b0b980c");
		
		//mock response
		mockBackEnd.enqueue(
				generateTableResponseMock(
						table.getAlias(), 
						10,
						Slots.values().length,
						200));
		
		//test
		Table created = adminService.updateTable(table.getId(), table);
		assertEquals(table.getAlias(), created.getAlias());
		assertEquals(10, created.getAvailableSeats());
	}
	
	@Test
	void shouldDeleteTable() {
		//given
		Table table = new Table("Table01", 5);
		table.setId("605f84d0465f6a499b0b980c");
		
		//mock response
		mockBackEnd.enqueue(
				generateTableResponseMock(
						table.getAlias(), 
						10,
						Slots.values().length,
						200));
		
		//test
		assertDoesNotThrow(() -> {
			adminService.deleteTable(table.getId());
		});
	}

}
