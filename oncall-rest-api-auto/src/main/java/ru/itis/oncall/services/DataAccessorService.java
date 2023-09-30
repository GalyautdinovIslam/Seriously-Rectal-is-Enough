package ru.itis.oncall.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.extern.slf4j.Slf4j;
import ru.itis.oncall.models.FullData;
import ru.itis.oncall.models.Team;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class DataAccessorService {

    private final ObjectMapper objectMapper;

    public DataAccessorService() {
        this.objectMapper = new YAMLMapper();
    }

    // Получение всех данных из YAML-файла
    public List<Team> getTeams(String path) throws IOException {
        try {
            return objectMapper.readValue(new File(path), FullData.class).getTeams();
        } catch (JsonProcessingException e) {
            log.error("Не смогли преобразовать данные в объект FullData", e);
            throw e;
        } catch (IOException e) {
            log.error("Не смогли получить данные из YAML", e);
            throw e;
        } catch (Exception e) {
            log.error("ПРОБЛЕМА : DataAccessorService::getTeams", e);
            throw e;
        }
    }
}
