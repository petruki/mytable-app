package com.github.petruki.mytable.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.github.petruki.mytable.model.Table;
import com.github.petruki.mytable.service.facades.ApiClient;

@Service
public class AdminService {
	
	@Value("${api.url}")
	private String apiUrl;
	private ApiClient<Table> apiClient;
	
	public AdminService() {}
	
	public AdminService(String apiUrl) {
		apiClient = new ApiClient<>(apiUrl + "v1/admin", Table.class);
	}
	
	@PostConstruct
	private void initClient() {
		apiClient = new ApiClient<>(apiUrl + "v1/admin", Table.class);
	}
	
    public List<Table> listTables() {
        return apiClient.doGetAll();
    }
    
	public Table findTable(String id) {
		if (id.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		return apiClient.doGetOne(id);
	}
	
	public Table createTable(Table table) {
		if (table == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		return apiClient.doPost(table, "/create");
	}
	
	public Table updateTable(String id, Table table) {
		if (id.isBlank() || table == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		return apiClient.doPut(table, id);
	}
	
	public void deleteTable(String id) {
		if (id.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		apiClient.doDelete(id);
	}

}
