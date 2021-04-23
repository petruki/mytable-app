package com.github.petruki.mytable.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.petruki.mytable.model.Slots;
import com.github.petruki.mytable.model.Table;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
class AppControllerTest {
	
	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	private static MockWebServer mockBackEnd;
	
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
	static void setupBackend() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        
        System.setProperty("api.url", "http://localhost:" + mockBackEnd.getPort() + "/");
    }
	
	@BeforeEach
	void setupBackendMock() {
		//given
		Table table = new Table("Table01", 5);
				
		//mock response
		mockBackEnd.enqueue(
				generateListTableResponseMock(
						table.getAlias(), 
						table.getAvailableSeats(), 
						Slots.values().length,
						200));
	}
	
	@PostConstruct
	void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}
	
	@Test
	void shouldLoginAsAdmin() throws Exception {
		this.mockMvc
			.perform(get("/")
					.with(user("admin").password("admin").roles("ADMIN")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Bookings Today")))
			.andExpect(content().string(containsString("Logged as admin")));
	}
	
	@Test
	void shouldBeAllowedVisitAdminPage() throws Exception  {
		this.mockMvc
			.perform(get("/admin")
					.with(user("admin").password("admin").roles("ADMIN")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(
					containsString("<h2 class=\"my-5\">Tables</h2>")));
	}
	
	@Test
	void shouldNotBeAllowedVisitAdminPage() throws Exception {
		this.mockMvc
			.perform(get("/admin")
					.with(user("clerk").password("clerk").roles("CLERK")))
			.andDo(print())
			.andExpect(status().isForbidden());
	}
	
	@Test
	void shouldVisitBookingAsAdmin() throws Exception  {
		this.mockMvc
			.perform(get("/booking")
					.with(user("admin").password("admin").roles("ADMIN")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(
					containsString("<h2 class=\"my-5\">Bookings</h2>")));
	}
	
	@Test
	void shouldVisitBookingAsClerk() throws Exception  {
		this.mockMvc
			.perform(get("/booking")
					.with(user("clerk").password("clerk").roles("CLERK")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(
					containsString("<h2 class=\"my-5\">Bookings</h2>")));
	}

}
