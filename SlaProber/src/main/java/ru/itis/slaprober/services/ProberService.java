package ru.itis.slaprober.services;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProberService {

    private final RequestService requestService;
    private final MeterRegistry meterRegistry;

    @Scheduled(fixedRate = 15000)
    public void getTeamsProbe() {
        try {
            ResponseEntity<List<Object>> response = requestService.getTeams();
            if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
                log.info("SUCCESS getTeams");
                meterRegistry.counter("sla_prober_get_teams_success").increment();
            } else {
                log.warn("FAIL getTeams");
                meterRegistry.counter("sla_prober_get_teams_fail").increment();
            }
        } catch (Exception exception) {
            log.error("FAIL getTeams", exception);
            meterRegistry.counter("sla_prober_get_teams_fail").increment();
        }
    }

    @Scheduled(fixedRate = 15000)
    public void createUserProbe() {
        try {
            ResponseEntity<?> response = requestService.createUser("create_user_probe");
            if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(201))) {
                log.info("User created");
                meterRegistry.counter("sla_prober_create_user_success").increment();
            } else {
                log.warn("Problems in creating user");
                meterRegistry.counter("sla_prober_create_user_fail").increment();
            }
        } catch (Exception exception) {
            log.error("Problems in creating user", exception);
            meterRegistry.counter("sla_prober_create_user_fail").increment();
        } finally {
            try {
                requestService.removeUser("create_user_probe");
            } catch (Exception ignored) {
            }
        }
    }

    @Scheduled(fixedRate = 15000)
    public void removeUserProbe() {
        try {
            requestService.createUser("remove_user_probe");
        } catch (Exception ignored) {
        }
        try {
            ResponseEntity<?> response = requestService.removeUser("remove_user_probe");
            List<Object> getUser = Optional.of(requestService.getUser("remove_user_probe"))
                    .map(HttpEntity::getBody)
                    .orElseThrow(() -> new RuntimeException("Body is null"));
            if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)) && getUser.isEmpty()) {
                log.info("SUCCESS removeUser");
                meterRegistry.counter("sla_prober_remove_user_success").increment();
            } else {
                log.warn("Problems in removing user");
                meterRegistry.counter("sla_prober_remove_user_fail").increment();
            }
        } catch (Exception exception) {
            log.error("Problems in removing user", exception);
            meterRegistry.counter("sla_prober_remove_user_fail").increment();
        }
    }

}
