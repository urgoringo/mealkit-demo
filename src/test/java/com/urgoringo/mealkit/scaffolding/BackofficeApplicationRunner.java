package com.urgoringo.mealkit.scaffolding;

import com.urgoringo.mealkit.auth.PasswordHasher;
import com.urgoringo.mealkit.auth.TokenService;
import com.urgoringo.mealkit.backoffice.domain.BackofficeUser;
import com.urgoringo.mealkit.backoffice.domain.BackofficeUsers;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@NullMarked
@Component
@RequiredArgsConstructor
public class BackofficeApplicationRunner {

    private final RestClient.Builder restClientBuilder;
    private final TokenService tokenService;
    private final PasswordHasher passwordHasher;
    private final BackofficeUsers backofficeUsers;

    private RestClient restClient;
    private int currentPort = -1;

    public void start(int port) {
        if (restClient == null || currentPort != port) {
            this.restClient = restClientBuilder
                .baseUrl("http://localhost:" + port)
                .build();
            currentPort = port;
        }
    }

    public void reset() {
        backofficeUsers.deleteAll();
    }

    public void markOrderDelivered(String orderId) {
        tryMarkOrderDelivered(orderId).expectSuccess();
    }

    public ApiResponse<Void> tryMarkOrderDelivered(String orderId) {
        var backofficeUser = getOrCreateBackofficeUser();
        String backofficeToken = tokenService.generateBackofficeToken(backofficeUser);
        
        var response = restClient.post()
            .uri("/orders/{orderId}/delivered", orderId)
            .header("Authorization", "Bearer " + backofficeToken)
            .retrieve()
            .toBodilessEntity();
        
        return ApiResponse.from(response);
    }

    private BackofficeUser getOrCreateBackofficeUser() {
        return backofficeUsers.findByEmail("backoffice@mealkit.com")
                .orElseGet(() -> {
                    String hashedPassword = passwordHasher.hash("backoffice-password");
                    return backofficeUsers.add(
                            BackofficeUser.create(
                                    "backoffice@mealkit.com",
                                    hashedPassword
                            )
                    );
                });
    }
}
