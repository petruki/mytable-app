package com.github.petruki.mytable.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.github.petruki.mytable.model.Booking;
import com.github.petruki.mytable.model.Customer;
import com.github.petruki.mytable.model.Slots;
import com.github.petruki.mytable.model.Table;
import com.github.petruki.mytable.service.BookingService;

@Controller
public class BookingController {
	
    private final BookingService bookingService;
    private String tableAliasSelected;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
	
    @GetMapping("/booking")
    public String showBooking(Model model, Table table) {
    	model.addAttribute("view", "booking");
        model.addAttribute("tables", bookingService.listTables());
        return "index";
    }

    @GetMapping("/booking/table/{id}")
    public String showTableBooking(@PathVariable("id") String id, Model model) {
        Table table = bookingService.findTable(id);
        tableAliasSelected = table.getAlias();
        
        List<String> availableSlots = Arrays.stream(Slots.values())
        		.map(Slots::toString) 
        		.collect(Collectors.toList());
        
        for (Booking booking : table.getBookings()) {
        	availableSlots.remove(booking.getSlot());
        }
        
        model.addAttribute("table", table);
        model.addAttribute("available_slots", availableSlots);
        model.addAttribute("booking", new Booking());
        model.addAttribute("view", "booking-table");
        return "index";
    }

    @PostMapping("/booking/add")
    public String addTable(@Valid Booking booking, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-booking";
        }

        bookingService.bookTable(tableAliasSelected, booking);
        return "redirect:/booking";
    }

    @PostMapping("/booking/cancel/{tableid}/{slot}")
    public String cancelTable(@PathVariable("tableid") String tableid, @PathVariable("slot") String slot, Model model) {
    	Table table = bookingService.findTable(tableid);
    	Customer customer = table.getBookings().stream()
    			.filter(b -> b.getSlot().equals(slot))
    			.findFirst().get().getCustomer();

        bookingService.cancelBooking(tableAliasSelected, customer);
        return "redirect:/booking";
    }

}
