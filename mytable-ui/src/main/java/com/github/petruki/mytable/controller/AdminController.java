package com.github.petruki.mytable.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.github.petruki.mytable.model.Table;
import com.github.petruki.mytable.service.AdminService;

@Controller
public class AdminController {
	
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @GetMapping("/admin")
    public String showAdmin(Model model, Table table) {
    	model.addAttribute("view", "admin");
    	model.addAttribute("tables", adminService.listTables());
        return "index";
    }
    
    @GetMapping("/admin/addtable-form")
    public String showAddTable(Model model, Table table) {
    	model.addAttribute("view", "add-table");
        return "index";
    }
    
    @GetMapping("/admin/table/edit/{id}")
    public String showEditTable(@PathVariable("id") String id, Model model) {
        Table table = adminService.findTable(id);
        model.addAttribute("table", table);
        model.addAttribute("view", "update-table");
        return "index";
    }
    
    @PostMapping("/admin/addtable")
    public String addTable(@Valid Table table, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-table";
        }
        
        adminService.createTable(table);
        return "redirect:/admin";
    }
    
    @PostMapping("/admin/table/update/{id}")
    public String updateTable(@PathVariable("id") String id, @Valid Table table, BindingResult result, Model model) {
        if (result.hasErrors()) {
            table.setId(id);
            return "app";
        }
        
        adminService.updateTable(id, table);
        return "redirect:/admin";
    }
    
    @GetMapping("/admin/table/delete/{id}")
    public String deleteTable(@PathVariable("id") String id, Model model) {
        adminService.deleteTable(id);
        return "redirect:/admin";
    }


}
