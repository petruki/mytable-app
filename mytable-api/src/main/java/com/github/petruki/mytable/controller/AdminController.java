package com.github.petruki.mytable.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.petruki.mytable.model.Table;
import com.github.petruki.mytable.service.TableService;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    private final TableService tableService;

    @Autowired
    public AdminController(TableService tableService) {
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
    
    @PostMapping("/create")
    public ResponseEntity<Table> createTable(@RequestBody Table table) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.createTable(table));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Table> updateTable(@PathVariable("id") String id, @RequestBody Table table) {
        return ResponseEntity.ok(tableService.updateTable(id, table));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTable(@PathVariable("id") String id) {
    	tableService.deleteTable(id);
        return ResponseEntity.ok().build();
    }
}
