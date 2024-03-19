package com.example.testing.controller;

import com.example.testing.dao.SupplierService;
import com.example.testing.model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/search-page")
    public String showSearchPage() {
        return "search-suppliers"; // return the name of your Thymeleaf template for the search page
    }

    @GetMapping("/search")
    public String searchSuppliersByName(@RequestParam String name, Model model) {
        List<Supplier> suppliers = supplierService.searchSupplierByName(name);
        model.addAttribute("suppliers", suppliers);
        return "search-suppliers";
    }
}

