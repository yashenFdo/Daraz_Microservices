package lk.daraz.userservice.mapper;

import lk.daraz.userservice.dto.CustomerResponse;
import lk.daraz.userservice.dto.RegisterRequest;
import lk.daraz.userservice.entity.Customer;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between Customer entity and DTOs.
 * Password is intentionally excluded from CustomerResponse mapping.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    /**
     * Maps a Customer entity to a CustomerResponse DTO.
     */
    CustomerResponse toResponse(Customer customer);

    /**
     * Maps a RegisterRequest to a Customer entity.
     * Password encoding is handled by AuthService before calling this.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    Customer toEntity(RegisterRequest request);

    /**
     * Partially updates an existing Customer from an update request.
     * Null fields in the source are ignored (PATCH semantics).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void partialUpdate(@MappingTarget Customer customer, lk.daraz.userservice.dto.UpdateProfileRequest request);
}
