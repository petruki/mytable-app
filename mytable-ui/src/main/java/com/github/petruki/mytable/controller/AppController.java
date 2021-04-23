package com.github.petruki.mytable.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.petruki.mytable.model.Table;
import com.github.petruki.mytable.service.BookingService;

@Controller
public class AppController {
	
	private String currentView;
    private final BookingService bookingService;

    @Autowired
    public AppController(BookingService bookingService) {
        this.bookingService = bookingService;
        this.currentView = "home";

    }
	
    @GetMapping("/")
    public String landingPage(Model model, Table table) {
    	model.addAttribute("view", currentView);
        model.addAttribute("tables", bookingService.listTables());
        return "index";
    }

}
