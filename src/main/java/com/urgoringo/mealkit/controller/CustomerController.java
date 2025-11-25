package com.urgoringo.mealkit.controller;

import com.urgoringo.mealkit.domain.Customer;
import com.urgoringo.mealkit.service.LoginCustomerService;
import com.urgoringo.mealkit.service.SignupCustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@NullMarked
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final SignupCustomerService signupCustomerService;
    private final LoginCustomerService loginCustomerService;

    @PostMapping("/signup")
    public ResponseEntity<CustomerResponse> signup(@Valid @RequestBody SignupRequest request) {
        Customer customer = signupCustomerService.execute(request.email(), request.password());
        CustomerResponse response = new CustomerResponse(customer.id().value(), customer.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = loginCustomerService.execute(request.email(), request.password());
        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(response);
    }

    public record SignupRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record CustomerResponse(Long id, String email) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record LoginResponse(String token) {}
}
