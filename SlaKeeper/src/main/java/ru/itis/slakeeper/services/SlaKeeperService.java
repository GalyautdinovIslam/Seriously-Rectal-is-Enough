package ru.itis.slakeeper.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.itis.slakeeper.models.PromAnswer;
import ru.itis.slakeeper.models.Sla;
import ru.itis.slakeeper.repositories.SlaRepository;

import java.time.LocalDateTime;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlaKeeperService {

    private final RestTemplate restTemplate;
    private final SlaRepository slaRepository;

    @Scheduled(fixedRate = 60000)
    public void keepGetTeamsSuccess() {
        log.info("sla_prober_get_teams_success run");
        keepTotal("sla_prober_get_teams_success", 0d, v -> 3.5 < v && v < 4.5);
    }

    @Scheduled(fixedRate = 60000)
    public void keepGetTeamsFail() {
        log.info("sla_prober_get_teams_fail run");
        keepTotal("sla_prober_get_teams_fail", 1d, v -> v < 0.5);
    }

    @Scheduled(fixedRate = 60000)
    public void keepCreateUserSuccess() {
        log.info("sla_prober_create_user_success run");
        keepTotal("sla_prober_create_user_success", 0d, v -> 3.5 < v && v < 4.5);
    }

    @Scheduled(fixedRate = 60000)
    public void keepCreateUserFail() {
        log.info("sla_prober_create_user_fail run");
        keepTotal("sla_prober_create_user_fail", 1d, v -> v < 0.5);
    }

    @Scheduled(fixedRate = 60000)
    public void keepRemoveUserSuccess() {
        log.info("sla_prober_remove_user_success run");
        keepTotal("sla_prober_remove_user_success", 0d, v -> 3.5 < v && v < 4.5);
    }

    @Scheduled(fixedRate = 60000)
    public void keeRemoveUserFail() {
        log.info("sla_prober_remove_user_fail run");
        keepTotal("sla_prober_remove_user_fail", 1d, v -> v < 0.5);
    }


    private void keepTotal(String name, Double def, Function<Double, Boolean> checker) {
        keepOne(name + "_total", def, checker);
    }

    private void keepOne(String name, Double def, Function<Double, Boolean> checker) {
        try {
            ResponseEntity<PromAnswer> response = restTemplate.exchange(
                    "http://localhost:9090/api/v1/query?query=increase(" + name + "[1m])",
                    HttpMethod.GET,
                    null,
                    PromAnswer.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("NOT 2xx Status Code from Prometheus");
            }
            PromAnswer body = response.getBody();
            if (body == null) {
                throw new RuntimeException("Response body is null");
            }
            slaRepository.saveAndFlush(Sla.builder()
                    .name(name)
                    .localDateTime(body.getTime())
                    .value(body.getValue())
                    .isGood(checker.apply(body.getValue()))
                    .build());
        } catch (RuntimeException exception) {
            log.error("Some problems with SLA-query", exception);
            slaRepository.saveAndFlush(Sla.builder()
                    .name(name)
                    .localDateTime(LocalDateTime.now())
                    .value(def)
                    .isGood(checker.apply(def))
                    .build());
        }
    }
}
