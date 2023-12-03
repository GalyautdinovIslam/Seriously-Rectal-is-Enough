package ru.itis.slaprober.services;

import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.itis.slaprober.dto.LoginResponse;

import java.util.Optional;

@Getter
@Component
public class CsrfKeeper {

    private final String csrf;

    public CsrfKeeper(RestTemplate restTemplate) {
        ResponseEntity<LoginResponse> response = restTemplate.exchange(
                "http://localhost:8080/login",
                HttpMethod.POST,
                // TODO: remove password
                new HttpEntity<String>("username=root&password=root"),
                LoginResponse.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            // TODO: use custom exception
            throw new RuntimeException("Не смогли авторизоваться");
        }

        csrf = Optional.of(response)
                .map(HttpEntity::getBody)
                .map(LoginResponse::getCsrfToken)
                // TODO: use custom exception
                .orElseThrow(() -> new RuntimeException("CSRF is null"));
    }
}
