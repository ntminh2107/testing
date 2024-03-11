package com.example.testing.controller;

import com.example.testing.dao.CategoryService;
import com.example.testing.dao.ProductService;
import com.example.testing.dao.SupplierService;
import com.example.testing.model.Category;
import com.example.testing.model.Product;
import com.example.testing.model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController

public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, SupplierService supplierService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
    }

    // Handler method to display the add product form
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);
        return "add-product";
    }

    // Handler method to process the form submission and add the product
    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") Product product) {
        productService.addProduct(product);
        return "redirect:/products/list";
    }

    // Handler method to display the list of products
    @GetMapping("/list")
    public String showProductList(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product-list";
    }
    @GetMapping("/option")
    public String showProductOptionsPage() {
        return "product-options"; // This should be the name of your product options page template
    }
}

