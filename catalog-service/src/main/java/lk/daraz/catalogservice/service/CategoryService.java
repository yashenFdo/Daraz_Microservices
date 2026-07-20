package lk.daraz.catalogservice.service;

import lk.daraz.catalogservice.document.ProductCategory;
import lk.daraz.catalogservice.dto.CategoryResponse;
import lk.daraz.catalogservice.dto.CreateCategoryRequest;
import lk.daraz.catalogservice.exception.CategoryNotFoundException;
import lk.daraz.catalogservice.mapper.CatalogMapper;
import lk.daraz.catalogservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CatalogMapper catalogMapper;

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        ProductCategory category = catalogMapper.toCategory(request);
        category.setActive(true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        ProductCategory saved = categoryRepository.save(category);
        log.info("Category created: id={}, name={}", saved.getId(), saved.getName());
        return catalogMapper.toCategoryResponse(saved);
    }

    public CategoryResponse getCategoryById(String id) {
        return categoryRepository.findById(id)
                .map(catalogMapper::toCategoryResponse)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(catalogMapper::toCategoryResponse)
                .toList();
    }

    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentCategoryIdIsNull().stream()
                .map(catalogMapper::toCategoryResponse)
                .toList();
    }

    public List<CategoryResponse> getSubCategories(String parentId) {
        return categoryRepository.findByParentCategoryId(parentId).stream()
                .map(catalogMapper::toCategoryResponse)
                .toList();
    }
}
