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

    public void start(int port) {
        this.restClient = restClientBuilder
            .baseUrl("http://localhost:" + port)
            .build();
    }

    public void reset() {
        backofficeUsers.deleteAll();
    }

    public void markOrderDelivered(Long orderId) {
        var backofficeUser = getOrCreateBackofficeUser();
        String backofficeToken = tokenService.generateBackofficeToken(backofficeUser);
        
        restClient.post()
            .uri("/orders/{orderId}/delivered", orderId)
            .header("Authorization", "Bearer " + backofficeToken)
            .retrieve()
            .toBodilessEntity();
    }

    private BackofficeUser getOrCreateBackofficeUser() {
        return backofficeUsers.findByEmail("backoffice@mealkit.com")
                .orElseGet(() -> {
                    String hashedPassword = passwordHasher.hash("backoffice-password");
                    return backofficeUsers.save(
                            BackofficeUser.create(
                                    "backoffice@mealkit.com",
                                    hashedPassword
                            )
                    );
                });
    }
}
