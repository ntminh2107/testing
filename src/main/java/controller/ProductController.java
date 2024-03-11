package controller;

import dao.CategoryService;
import dao.ProductService;
import dao.SupplierService;
import model.Category;
import model.Product;
import model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/products")
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
    @GetMapping
    public String showProductOptionsPage() {
        return "product-options"; // This should be the name of your product options page template
    }
}

