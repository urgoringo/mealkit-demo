package com.urgoringo.mealkit.cucumber.steps;

import com.urgoringo.mealkit.cucumber.ApiResponse;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class LastResponseState {
    private ApiResponse<?> lastResponse;

    public <T> ApiResponse<T> getLastResponse() {
        return (ApiResponse<T>) lastResponse;
    }

    public <T> T getLastResponseExpectSuccess() {
        return (T) getLastResponse().expectSuccess();
    }

    public void setLastResponse(ApiResponse<?> lastResponse) {
        this.lastResponse = lastResponse;
    }
}
