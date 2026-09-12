package com.novabank.customerservice.controller;

import com.novabank.customerservice.dto.CustomerResponse;
import com.novabank.customerservice.service.CustomerService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/me")
    public CustomerResponse getOwnProfile(Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        return customerService.getById(customerId);
    }

    /**
     * Internal lookup, called by account-service when it needs profile data
     * beyond what's already in the caller's JWT.
     */
    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return customerService.getById(id);
    }
}
