package com.urgoringo.mealkit.customer.api;

import com.urgoringo.mealkit.customer.application.application.LoginCustomerService;
import com.urgoringo.mealkit.customer.application.application.SignupCustomerService;
import com.urgoringo.mealkit.customer.domain.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final SignupCustomerService signupCustomerService;
    private final LoginCustomerService loginCustomerService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        Customer customer = signupCustomerService.execute(request.email(), request.password());
        String token = loginCustomerService.execute(request.email(), request.password());
        SignupResponse response = new SignupResponse(customer.id().value().toString(), customer.email(), token);
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

    public record SignupResponse(String id, String email, String token) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record LoginResponse(String token) {}
}
