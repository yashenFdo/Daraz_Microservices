package lk.daraz.userservice.controller;

import jakarta.validation.Valid;
import lk.daraz.userservice.dto.CustomerResponse;
import lk.daraz.userservice.dto.UpdateProfileRequest;
import lk.daraz.userservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * GET /api/v1/customers/me
     * Returns the authenticated customer's own profile.
     */
    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(userDetails.getUsername()));
    }

    /**
     * GET /api/v1/customers/{id}
     * Returns a specific customer profile by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    /**
     * GET /api/v1/customers  (Admin only)
     * Returns a paginated list of all customers.
     */
    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }

    /**
     * PATCH /api/v1/customers/{id}
     * Partially updates a customer profile (PATCH semantics).
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(customerService.updateProfile(id, request));
    }

    /**
     * DELETE /api/v1/customers/{id}  (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
