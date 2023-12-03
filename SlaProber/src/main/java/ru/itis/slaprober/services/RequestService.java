package ru.itis.slaprober.services;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.itis.slaprober.dto.UserRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final CsrfKeeper csrfKeeper;
    private final RestTemplate restTemplate;

    // TODO: don't use Object class for this
    public ResponseEntity<List<Object>> getTeams() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Csrf-Token", csrfKeeper.getCsrf());
        return restTemplate.exchange(
                "http://localhost:8080/api/v0/teams",
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public ResponseEntity<?> createUser(String username) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Csrf-Token", csrfKeeper.getCsrf());
        return restTemplate.exchange(
                "http://localhost:8080/api/v0/users",
                HttpMethod.POST,
                new HttpEntity<>(new UserRequest(username), httpHeaders),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public ResponseEntity<?> removeUser(String username) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Csrf-Token", csrfKeeper.getCsrf());
        return restTemplate.exchange(
                "http://localhost:8080/api/v0/users/" + username,
                HttpMethod.DELETE,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public ResponseEntity<List<Object>> getUser(String username) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Csrf-Token", csrfKeeper.getCsrf());
        return restTemplate.exchange(
                "http://localhost:8080/api/v0/users?name=" + username,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<>() {
                }
        );
    }
}
