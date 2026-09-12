package com.novabank.customerservice.service;

import com.novabank.customerservice.dto.*;
import com.novabank.customerservice.entity.Customer;
import com.novabank.customerservice.exception.CustomerNotFoundException;
import com.novabank.customerservice.exception.EmailAlreadyExistsException;
import com.novabank.customerservice.exception.InvalidCredentialsException;
import com.novabank.customerservice.repository.CustomerRepository;
import com.novabank.customerservice.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public CustomerService(CustomerRepository customerRepository,
                            PasswordEncoder passwordEncoder,
                            JwtService jwtService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public CustomerResponse register(RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setPhoneNumber(request.getPhoneNumber());

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public LoginResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!"ACTIVE".equals(customer.getStatus())) {
            throw new InvalidCredentialsException("Account is not active");
        }

        String token = jwtService.generateToken(customer.getId(), customer.getEmail());
        return new LoginResponse(token, jwtService.getExpirationSeconds());
    }

    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + id));
        return toResponse(customer);
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(c.getId(), c.getFullName(), c.getEmail(),
                c.getPhoneNumber(), c.getStatus(), c.getCreatedAt());
    }
}
