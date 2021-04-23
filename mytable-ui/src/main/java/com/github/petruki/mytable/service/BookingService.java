package com.github.petruki.mytable.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.github.petruki.mytable.model.Booking;
import com.github.petruki.mytable.model.Customer;
import com.github.petruki.mytable.model.Table;
import com.github.petruki.mytable.service.facades.ApiClient;

import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {
	
	@Value("${api.url}")
	private String apiUrl;
	private ApiClient<Table> apiClient;
	
	public BookingService() {}
	
	public BookingService(String apiUrl) {
		apiClient = new ApiClient<>(apiUrl + "v1/booking", Table.class);
	}
	
	@PostConstruct
	private void initClient() {
		apiClient = new ApiClient<>(apiUrl + "v1/booking", Table.class);
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
	public Table bookTable(String alias, Booking booking) {
		return apiClient.doPost(booking, alias);
	}
	
	public Table cancelBooking(String alias, Customer customer) {
		return apiClient.doPost(customer, "cancel/" + alias);
	}

}
