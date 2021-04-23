package com.github.petruki.mytable.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.petruki.mytable.model.Booking;
import com.github.petruki.mytable.model.Customer;
import com.github.petruki.mytable.model.Table;
import com.github.petruki.mytable.service.TableService;

@RestController
@RequestMapping("api/v1/booking")
public class BookingController {
	
    private final TableService tableService;

    @Autowired
    public BookingController(TableService tableService) {
        this.tableService = tableService;
    }
    
    @GetMapping("/")
    public ResponseEntity<List<Table>> listTables() {
        return ResponseEntity.ok(tableService.listTables());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Table> findTable(@PathVariable("id") String id) {
        return ResponseEntity.ok(tableService.findTableById(id));
    }
    
    @PostMapping("/{alias}")
    public ResponseEntity<Table> bookTable(
    		@PathVariable("alias") String alias, 
    		@RequestBody Booking booking) {
        return ResponseEntity.ok().body(tableService.bookTable(alias, booking));
    }
    
    @PostMapping("/cancel/{alias}")
    public ResponseEntity<Table> cancelBooking(
    		@PathVariable("alias") String alias, 
    		@RequestBody Customer customer) {
        return ResponseEntity.ok().body(tableService.cancelBooking(alias, customer));
    }


}
