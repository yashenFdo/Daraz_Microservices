package lk.daraz.userservice.service;

import lk.daraz.userservice.dto.AuthResponse;
import lk.daraz.userservice.dto.LoginRequest;
import lk.daraz.userservice.dto.RegisterRequest;
import lk.daraz.userservice.entity.Customer;
import lk.daraz.userservice.entity.Role;
import lk.daraz.userservice.event.UserEventProducer;
import lk.daraz.userservice.event.UserRegisteredEvent;
import lk.daraz.userservice.exception.CustomerAlreadyExistsException;
import lk.daraz.userservice.mapper.CustomerMapper;
import lk.daraz.userservice.repository.CustomerRepository;
import lk.daraz.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication service: handles user registration and login.
 * Issues JWT access + refresh tokens on success.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserEventProducer userEventProducer;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException(request.getEmail(), true);
        }

        Customer customer = customerMapper.toEntity(request);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setRole(Role.ROLE_CUSTOMER);
        customer.setEnabled(true);

        Customer saved = customerRepository.save(customer);
        log.info("New customer registered: id={}, email={}", saved.getId(), saved.getEmail());

        // Publish async event for notification service
        userEventProducer.publishUserRegistered(
                UserRegisteredEvent.of(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getEmail())
        );

        String accessToken = jwtService.generateToken(saved);
        String refreshToken = jwtService.generateRefreshToken(saved);

        return AuthResponse.of(accessToken, refreshToken, jwtService.getJwtExpiration(),
                customerMapper.toResponse(saved));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Customer not found after authentication"));

        log.info("Customer logged in: id={}, email={}", customer.getId(), customer.getEmail());

        String accessToken = jwtService.generateToken(customer);
        String refreshToken = jwtService.generateRefreshToken(customer);

        return AuthResponse.of(accessToken, refreshToken, jwtService.getJwtExpiration(),
                customerMapper.toResponse(customer));
    }
}
