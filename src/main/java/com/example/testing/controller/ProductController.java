package com.example.testing.controller;

import com.example.testing.dao.*;
import com.example.testing.model.Category;
import com.example.testing.model.Product;
import com.example.testing.model.Supplier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, SupplierService supplierService, ProductRepository productRepository, CategoryRepository categoryRepository, SupplierRepository supplierRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
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
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // If there are validation errors, return the form with errors
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }
            return "add-product";
        }

        productService.addProduct(product);
        return "redirect:/products/add";
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
        return "options"; // This should be the name of your product options page template
    }
    @GetMapping("/search-page")
    public String showSearchProductPage() {
        return "search"; // This should be the name of your product options page template
    }
    @PostMapping("/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam String query) {
        List<Product> searchResults = productService.searchProductsByName(query);
        return searchResults;
    }
    @PostMapping("/delete/{productId}")
    @ResponseBody
    public String deleteProduct(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return "Product deleted successfully";
    }
    @GetMapping("/edit/{productId}")
    public String editProduct(@PathVariable Long productId, Model model) {
        // Retrieve existing product from the database
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        // Retrieve all categories and suppliers
        List<Category> categories = categoryRepository.findAll();
        List<Supplier> suppliers = supplierRepository.findAll();

        // Add product, categories, and suppliers to the model
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("suppliers", suppliers);

        return "edit-product"; // Assuming the name of the Thymeleaf template is "edit-product"
    }


    @PostMapping("/edit/{productId}")
    public String updateProduct(@PathVariable Long productId, @Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                Model model) {
        // Retrieve existing product from the database
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        // Check if the edited product name already exists
        Product existingProductWithName = productRepository.findByName(product.getName());
        if (existingProductWithName != null && !existingProductWithName.getId().equals(productId)) {
            // If a product with the same name exists and it's not the current product being edited
            // Add an error message to the binding result
            bindingResult.rejectValue("name", "duplicate", "Product name already exists");
        }

        if (bindingResult.hasErrors()) {
            // Validation errors occurred, return to the edit form with error messages
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("suppliers", supplierRepository.findAll());
            return "edit-product";
        }

        // Continue updating the product if no validation errors occurred

        // Update existing product properties
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());

        // Retrieve the existing category from the database by name
        Category existingCategory = categoryService.getCategoryByName(product.getCategory().getName());
        if (existingCategory != null) {
            // Set the existing category to the product
            existingProduct.setCategory(existingCategory);
        } else {
            // Handle case where category does not exist
            // You may choose to throw an exception or handle it differently
        }

        // Retrieve the existing supplier from the database by name
        Supplier existingSupplier = supplierService.getSupplierByName(product.getSupplier().getName());
        if (existingSupplier != null) {
            // Set the existing supplier to the product
            existingProduct.setSupplier(existingSupplier);
        } else {
            // Handle case where supplier does not exist
            // You may choose to throw an exception or handle it differently
        }

        // Save the updated product
        productRepository.save(existingProduct);

        // Redirect to a suitable endpoint after successful update
        return "redirect:/products/search-page";
    }


    @GetMapping("/search-product-by-supplier")
    public String searchProductsBySupplierAndName(@RequestParam("supplierId") Long supplierId,
                                                  @RequestParam("productName") String productName,
                                                  Model model) {
        List<Product> products = productService.getProductsBySupplierIdAndName(supplierId, productName);
        model.addAttribute("products", products);
        return "product-search-by-supplier";
    }
    @PostMapping("/add-quantity")
    public String addQuantityToProduct(@RequestParam("selectedProduct") Long productId,
                                       @RequestParam("quantity") int quantity,
                                       @RequestParam("supplierId") Long supplierId,
                                       RedirectAttributes redirectAttributes) {
        try {
            // Retrieve the product from the database using its ID
            Product product = productService.getProductById(productId);

            // Add the specified quantity to the existing stock quantity of the product
            product.setStockQuantity(product.getStockQuantity() + quantity);

            // Save the updated product
            productService.saveProduct(product);

            // Redirect with success message
            redirectAttributes.addFlashAttribute("successMessage", "Quantity added successfully!");
        } catch (Exception e) {
            // Redirect with error message if an error occurs
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding quantity: " + e.getMessage());
        }

        // Redirect back to the search results page with supplierId
        return "redirect:/products/search-product-by-supplier?supplierId=" + supplierId+"&productName=";
    }



}

