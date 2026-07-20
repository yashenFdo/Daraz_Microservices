package lk.daraz.catalogservice.service;

import lk.daraz.catalogservice.document.Product;
import lk.daraz.catalogservice.document.ProductCategory;
import lk.daraz.catalogservice.document.ProductReview;
import lk.daraz.catalogservice.dto.*;
import lk.daraz.catalogservice.event.ReviewAddedEvent;
import lk.daraz.catalogservice.event.ReviewEventProducer;
import lk.daraz.catalogservice.exception.ProductNotFoundException;
import lk.daraz.catalogservice.exception.DuplicateSkuException;
import lk.daraz.catalogservice.mapper.CatalogMapper;
import lk.daraz.catalogservice.repository.CategoryRepository;
import lk.daraz.catalogservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogMapper catalogMapper;
    private final ReviewEventProducer reviewEventProducer;

    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }
        Product product = catalogMapper.toProduct(request);
        product.setActive(true);

        // Enrich with category name if exists
        categoryRepository.findById(request.getCategoryId()).ifPresent(cat ->
                product.setCategoryName(cat.getName()));

        Product saved = productRepository.save(product);
        log.info("Product created: id={}, sku={}", saved.getId(), saved.getSku());
        return catalogMapper.toProductResponse(saved);
    }

    public ProductResponse getProductById(String id) {
        return productRepository.findById(id)
                .map(catalogMapper::toProductResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public ProductResponse getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(catalogMapper::toProductResponse)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable).map(catalogMapper::toProductResponse);
    }

    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable).map(catalogMapper::toProductResponse);
    }

    public Page<ProductResponse> getProductsByCategory(String categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable).map(catalogMapper::toProductResponse);
    }

    public ProductResponse addReview(String productId, AddReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        ProductReview review = ProductReview.builder()
                .reviewId(UUID.randomUUID().toString())
                .customerId(request.getCustomerId())
                .customerName(request.getCustomerName())
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .verified(false)
                .createdAt(LocalDateTime.now())
                .build();

        product.getReviews().add(review);

        // Recalculate average rating
        double avg = product.getReviews().stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);
        product.setAverageRating(Math.round(avg * 10.0) / 10.0);
        product.setReviewCount(product.getReviews().size());

        Product saved = productRepository.save(product);
        log.info("Review added to product: id={}, customerId={}", productId, request.getCustomerId());

        // Publish Kafka event
        reviewEventProducer.publishReviewAdded(
                ReviewAddedEvent.of(productId, request.getCustomerId(), request.getRating())
        );

        return catalogMapper.toProductResponse(saved);
    }

    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setActive(false);
        productRepository.save(product);
        log.info("Product soft-deleted: id={}", id);
    }
}
