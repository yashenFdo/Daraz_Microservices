package lk.daraz.catalogservice.repository;

import lk.daraz.catalogservice.document.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Product> findByCategoryId(String categoryId, Pageable pageable);

    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("{ 'name': { $regex: ?0, $options: 'i' }, 'active': true }")
    Page<Product> findByNameContaining(String keyword, Pageable pageable);

    @Query("{ 'price': { $gte: ?0, $lte: ?1 }, 'active': true }")
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByBrandAndActiveTrue(String brand, Pageable pageable);
}
