package lk.daraz.catalogservice.repository;

import lk.daraz.catalogservice.document.ProductCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<ProductCategory, String> {

    Optional<ProductCategory> findByName(String name);

    boolean existsByName(String name);

    List<ProductCategory> findByParentCategoryIdIsNull();

    List<ProductCategory> findByParentCategoryId(String parentCategoryId);

    List<ProductCategory> findByActiveTrue();
}
