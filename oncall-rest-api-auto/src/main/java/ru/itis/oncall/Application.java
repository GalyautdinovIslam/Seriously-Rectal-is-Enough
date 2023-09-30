package ru.itis.oncall;

import lombok.extern.slf4j.Slf4j;
import ru.itis.oncall.services.DataAccessorService;
import ru.itis.oncall.services.clients.OncallClient;

import java.io.IOException;
import java.text.ParseException;

@Slf4j
public class Application {
    public static void main(String[] args) {
        try {
            OncallClient oncallClient = new OncallClient(args[0]);
            DataAccessorService dataAccessorService = new DataAccessorService();
            try {
                dataAccessorService.getTeams(args[1]).forEach(
                        team -> {
                            // Создаем команду
                            try {
                                oncallClient.createTeam(team);
                            } catch (IOException e) {
                                handleException(e);
                            }

                            // Создаем ростер
                            try {
                                oncallClient.createRoster(team);
                            } catch (IOException e) {
                                handleException(e);
                            }
                            team.getUsers().forEach(
                                    user -> {
                                        // Создаем пользователя
                                        try {
                                            oncallClient.createUser(user);
                                        } catch (IOException e) {
                                            handleException(e);
                                        }
                                        // Заполняем пользователя
                                        try {
                                            oncallClient.updateUser(user);
                                        } catch (IOException e) {
                                            handleException(e);
                                        }
                                        // Добавляем пользователя в команду
                                        try {
                                            oncallClient.addUserToTeam(team, user);
                                        } catch (IOException e) {
                                            handleException(e);
                                        }
                                        // Добавляем пользователя в ростер
                                        try {
                                            oncallClient.addUserToRoster(team, user);
                                        } catch (IOException e) {
                                            handleException(e);
                                        }
                                        user.getDuty().forEach(
                                                duty -> {
                                                    // Добавляем событие
                                                    try {
                                                        oncallClient.createDuty(team, user, duty);
                                                    } catch (IOException e) {
                                                        handleException(e);
                                                    } catch (ParseException e) {
                                                        log.error("Кривое значение {}", duty.getDate(), e);
                                                    }
                                                }
                                        );
                                    }
                            );
                        }
                );
            } catch (Exception e) {
                log.error("Не смогли получить данные из YAML", e);
                System.exit(1);
            }
        } catch (Exception e) {
            log.error("Не смогли авторизоваться в Oncall за ROOT", e);
            System.exit(1);
        }
    }

    private static void handleException(Exception exception) {
        log.warn("Что-то пошло не так, но мы продолжаем", exception);
    }
}
