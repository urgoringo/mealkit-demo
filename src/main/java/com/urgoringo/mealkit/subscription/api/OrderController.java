package com.urgoringo.mealkit.subscription.api;

import com.urgoringo.mealkit.backoffice.domain.BackofficeUser;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.application.MarkOrderDeliveredService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final MarkOrderDeliveredService markOrderDeliveredService;

    @PreAuthorize("hasRole('BACKOFFICE')")
    @PostMapping("/{orderId}/delivered")
    public ResponseEntity<Void> deliverOrder(
            @AuthenticationPrincipal Id<BackofficeUser> backofficeUserId,
            @PathVariable String orderId) {
        markOrderDeliveredService.execute(backofficeUserId, Id.of(orderId));
        return ResponseEntity.noContent().build();
    }
}
