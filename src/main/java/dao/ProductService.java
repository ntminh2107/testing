package dao;

import model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    @Autowired
    public ProductService(ProductRepository productRepository, CategoryService categoryService, SupplierService supplierService) {
        this.productRepository = productRepository;
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
}
