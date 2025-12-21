package com.urgoringo.mealkit.subscription.api;

import com.urgoringo.mealkit.customer.domain.Customer;
import com.urgoringo.mealkit.domain.Id;
import com.urgoringo.mealkit.subscription.application.DeliverOrderService;
import com.urgoringo.mealkit.subscription.domain.UpcomingOrder;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@NullMarked
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final DeliverOrderService deliverOrderService;

    @PreAuthorize("hasRole('BACKOFFICE')")
    @PostMapping("/{orderId}/delivered")
    public ResponseEntity<Void> deliverOrder(@PathVariable Long orderId) {
        deliverOrderService.execute(Id.of(orderId));
        return ResponseEntity.noContent().build();
    }
}
