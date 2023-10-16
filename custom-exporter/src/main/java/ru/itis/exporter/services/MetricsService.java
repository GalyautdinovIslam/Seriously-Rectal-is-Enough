package ru.itis.exporter.services;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.Search;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.itis.exporter.models.Event;
import ru.itis.exporter.models.User;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final RestTemplate restTemplate;

    @Scheduled(fixedRate = 15000)
    public void usersWithoutFullContacts() {
        log.info("usersWithoutFullContacts is started");
        ResponseEntity<List<User>> exchange = restTemplate.exchange(
                "http://localhost:8080/api/v0/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Не смогли достучаться до API");
        }
        List<User> users = exchange.getBody();
        if (users == null) {
            throw new RuntimeException("Ничего не получили от API");
        }
        long count = users.stream()
                .filter(user -> {
                    if (user.getContacts().getEmail() == null || user.getContacts().getCall() == null || user.getContacts().getSms() == null || user.getContacts().getSlack() == null) {
                        log.warn("No full contacts: {}", user.getName());
                        return true;
                    }
                    return false;
                })
                .count();
        Optional.of(meterRegistry.find("users_without_full_contacts"))
                .map(Search::meter)
                .ifPresent(meterRegistry::remove);
        meterRegistry.gauge("users_without_full_contacts", count);
        log.info("usersWithoutFullContacts is finished");
    }

    @Scheduled(fixedRate = 15000)
    public void noPrimaryToday() {
        log.info("noPrimaryToday is started");
        int count = 0;
        ResponseEntity<List<String>> exchangeTeams = restTemplate.exchange(
                "http://localhost:8080/api/v0/teams",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        if (!exchangeTeams.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Не смогли достучаться до API");
        }
        List<String> teams = exchangeTeams.getBody();
        if (teams == null) {
            throw new RuntimeException("Ничего не получили от API");
        }
        long unixTime = System.currentTimeMillis() / 1000L;
        for (String team : teams) {
            ResponseEntity<List<Event>> exchange = restTemplate.exchange(
                    "http://localhost:8080/api/v0/events?team=" + URLEncoder.encode(team, StandardCharsets.UTF_8) + "&start__lt=" + unixTime + "&end__gt=" + unixTime + "&role=primary",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            if (!exchange.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Не смогли достучаться до API");
            }
            List<Event> events = exchange.getBody();
            if (events == null) {
                throw new RuntimeException("Ничего не получили от API");
            }
            if (events.isEmpty()) {
                log.warn("No primary today: {}", team);
                count++;
            }
        }
        Optional.of(meterRegistry.find("no_primary_today"))
                .map(Search::meter)
                .ifPresent(meterRegistry::remove);
        meterRegistry.gauge("no_primary_today", count);
        log.info("noPrimaryToday is finished");
    }

    @Scheduled(fixedRate = 15000)
    public void primariesWithoutFullContacts() {
        log.info("primariesWithoutFullContacts is started");
        int count = 0;
        long unixTime = System.currentTimeMillis() / 1000L;
        ResponseEntity<List<Event>> exchangeEvents = restTemplate.exchange(
                "http://localhost:8080/api/v0/events?start__lt=" + unixTime + "&end__gt=" + unixTime + "&role=primary",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        if (!exchangeEvents.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Не смогли достучаться до API");
        }
        List<Event> events = exchangeEvents.getBody();
        if (events == null) {
            throw new RuntimeException("Ничего не получили от API");
        }
        for (Event event : events) {
            ResponseEntity<User> userResponseEntity = restTemplate.getForEntity("http://localhost:8080/api/v0/users/" + event.getUser(), User.class);
            if (!userResponseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Не смогли достучаться до API");
            }
            User user = userResponseEntity.getBody();
            if (user == null) {
                throw new RuntimeException("Ничего не получили от API");
            }
            if (user.getContacts().getEmail() == null || user.getContacts().getCall() == null || user.getContacts().getSms() == null || user.getContacts().getSlack() == null) {
                log.warn("Primary no full contacts: {}", user.getName());
                count++;
            }
        }
        Optional.of(meterRegistry.find("primaries_without_full_contacts"))
                .map(Search::meter)
                .ifPresent(meterRegistry::remove);
        meterRegistry.gauge("primaries_without_full_contacts", count);
        log.info("primariesWithoutFullContacts is finished");
    }
}
