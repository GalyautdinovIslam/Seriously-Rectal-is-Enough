package ru.itis.oncall.services.clients;

import lombok.extern.slf4j.Slf4j;
import ru.itis.oncall.dto.requests.DutyRequest;
import ru.itis.oncall.dto.requests.OnlyNameRequest;
import ru.itis.oncall.dto.requests.TeamRequest;
import ru.itis.oncall.dto.requests.UserRequest;
import ru.itis.oncall.dto.responses.LoginResponse;
import ru.itis.oncall.models.Duty;
import ru.itis.oncall.models.Team;
import ru.itis.oncall.models.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

@Slf4j
public class OncallClient {
    private final String api;
    private final HttpClient httpClient;
    private final String csrf;

    public OncallClient(String host) throws IOException {
        this.api = host + "/api/v0";
        this.httpClient = new HttpClient();
        LoginResponse response = httpClient.doMethodFormData(
                host + "/login",
                "POST",
                "username=root&password=root",
                LoginResponse.class
        );
        this.csrf = response.getCsrfToken();
    }

    // Создать команду
    public void createTeam(Team team) throws IOException {
        httpClient.doPost(
                api + "/teams",
                csrf,
                new TeamRequest(team),
                null
        );
    }

    // Создать ростер
    public void createRoster(Team team) throws IOException {
        httpClient.doPost(
                api + "/teams/" + URLEncoder.encode(team.getName(), StandardCharsets.UTF_8) + "/rosters",
                csrf,
                new OnlyNameRequest(team),
                null
        );
    }

    // Создать пользователя
    public void createUser(User user) throws IOException {
        httpClient.doPost(
                api + "/users",
                csrf,
                new OnlyNameRequest(user),
                null
        );
    }

    // Заполнить данные пользователя
    public void updateUser(User user) throws IOException {
        httpClient.doPut(
                api + "/users/" + URLEncoder.encode(user.getName(), StandardCharsets.UTF_8),
                csrf,
                new UserRequest(user),
                null
        );
    }

    // Добавить пользователя в команду
    public void addUserToTeam(Team team, User user) throws IOException {
        httpClient.doPost(
                api + "/teams/" + URLEncoder.encode(team.getName(), StandardCharsets.UTF_8)
                        + "/users",
                csrf,
                new OnlyNameRequest(user),
                null
        );
    }

    // Добавить пользователя в ростер
    public void addUserToRoster(Team team, User user) throws IOException {
        httpClient.doPost(
                api + "/teams/" + URLEncoder.encode(team.getName(), StandardCharsets.UTF_8)
                        + "/rosters/" + URLEncoder.encode(team.getName(), StandardCharsets.UTF_8)
                        + "/users",
                csrf,
                new OnlyNameRequest(user),
                null
        );
    }

    // Добавить событие
    public void createDuty(Team team, User user, Duty duty) throws IOException, ParseException {
        try {
            httpClient.doPost(
                    api + "/events",
                    csrf,
                    new DutyRequest(team, user, duty),
                    null
            );
        } catch (ParseException e) {
            log.error("Некорректное значение date {}", duty.getDate(), e);
            throw e;
        }
    }
}
