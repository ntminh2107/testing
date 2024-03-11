package dao;

import model.Category;
import model.Product;
import model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameAndSupplierAndCategory(String name, Supplier supplier, Category category);
}
