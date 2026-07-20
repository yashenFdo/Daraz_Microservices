package lk.daraz.catalogservice.mapper;

import lk.daraz.catalogservice.document.Product;
import lk.daraz.catalogservice.document.ProductCategory;
import lk.daraz.catalogservice.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CatalogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    Product toProduct(CreateProductRequest request);

    ProductResponse toProductResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductCategory toCategory(CreateCategoryRequest request);

    CategoryResponse toCategoryResponse(ProductCategory category);
}
