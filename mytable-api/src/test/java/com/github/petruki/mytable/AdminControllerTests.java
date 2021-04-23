package com.github.petruki.mytable;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.github.petruki.mytable.model.Table;
import com.github.petruki.mytable.service.TableService;
import com.google.gson.Gson;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureDataMongo
@AutoConfigureMockMvc
class AdminControllerTests {
	
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
		this.mockMvc.perform(get("/api/v1/admin/")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(table1.getAlias())))
			.andExpect(content().string(containsString(table2.getAlias())));
	}
	
	@Test
	void shouldCreateTable() throws Exception {
		//given
		Table table1 = new Table("Table03", 5);
		String jsonRequest = GSON.toJson(table1);
		
		//test
		this.mockMvc.perform(post("/api/v1/admin/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonRequest))
			.andExpect(status().isCreated());
	}
	
	@Test
	void shouldUpdateTable() throws Exception {
		//given
		Table userToUpdate = tableService.createTable(new Table("NEW_TABLE", 5));
		
		Table book = new Table("NEW_TABLE", 4);
		String jsonRequest = GSON.toJson(book);
		
		//test
		this.mockMvc.perform(put("/api/v1/admin/" + userToUpdate.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonRequest))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(String.valueOf(book.getAvailableSeats()))));
	}
	
	@Test
	void shouldDeleteTable() throws Exception {
		//given
		Table userToDelete = tableService.createTable(new Table("DEL_TABLE", 5));
		
		//test
		this.mockMvc.perform(delete("/api/v1/admin/" + userToDelete.getId())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
}
