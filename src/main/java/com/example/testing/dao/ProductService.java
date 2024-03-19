package com.example.testing.dao;

import com.example.testing.model.Category;
import com.example.testing.model.Product;
import com.example.testing.model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, CategoryService categoryService, SupplierService supplierService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
    public void addProduct(Product product) {
        Long categoryId = product.getCategory().getId();
        Long supplierId = product.getSupplier().getId();
        // Check if a product with the same name already exists for the given supplier and category
        boolean productExists = productRepository.existsByNameAndSupplierAndCategory(
                product.getName(), product.getSupplier(), product.getCategory());

        // If the product with the same name already exists, you can handle the error or validation accordingly
        if (productExists) {
            throw new IllegalArgumentException("A product with the same name already exists for the given supplier and category.");
        }

        // If the product does not exist, save it to the database

        product.setCategory(categoryService.getCategoryById(categoryId));
        product.setSupplier(supplierService.getSupplierById(supplierId));
        productRepository.save(product);
    }
    public List<Product> searchProductsByName(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }
    public void deleteProductById(Long productId) {
        productRepository.deleteById(productId);
    }
    public String updateProduct(Long productId, Product product) {
        // Retrieve existing product from the database
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        // Check if price or stock quantity is less than 1
        if (product.getPrice() < 1 || product.getStockQuantity() < 1) {
            return "Price and Stock Quantity must be greater than or equal to 1.";
        }

        // Update existing product properties
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());

        // Retrieve the existing category from the database by name
        Category existingCategory = categoryService.getCategoryByName(product.getCategory().getName());
        if (existingCategory == null) {
            // If the category does not exist, create a new one
            existingCategory = new Category();
            existingCategory.setName(product.getCategory().getName());
            // Save the new category to get its ID
            existingCategory = categoryService.saveCategory(existingCategory);
        }
        // Set the existing category to the product
        existingProduct.setCategory(existingCategory);

        // Retrieve the existing supplier from the database
        Supplier existingSupplier = supplierService.getSupplierById(product.getSupplier().getId());
        existingProduct.setSupplier(existingSupplier);

        // Save the updated product
        productRepository.save(existingProduct);

        // Return null or empty string if update is successful
        return "";
    }



    public boolean isDuplicateProduct(Product product) {
        // Check if a product with the same name, category, and supplier already exists
        return productRepository.existsByNameAndCategoryAndSupplier(product.getName(), product.getCategory(), product.getSupplier());
    }
    public List<Product> getProductsBySupplierIdAndName(Long supplierId, String productName) {
        return productRepository.findBySupplierIdAndNameContainingIgnoreCase(supplierId, productName);
    }
    public void saveProduct(Product product) {
        // Call the save method of ProductRepository
        productRepository.save(product);
    }
}
