package ru.itis.oncall.services.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@Slf4j
public class HttpClient {

    private static final String JSON = "application/json";
    private static final String FORM_DATA = "multipart/form-data";

    private final ObjectMapper objectMapper;

    public HttpClient() {
        this.objectMapper = new JsonMapper();
        CookieHandler.setDefault(new CookieManager());
    }

    // Сделать POST-запрос
    public <R> R doPost(String urlString, String csrf, Object body, Class<R> responseClass) throws IOException {
        return doMethodJson(urlString, "POST", csrf, body, responseClass);
    }

    // Сделать PUT-запрос
    public <R> R doPut(String urlString, String csrf, Object body, Class<R> responseClass) throws IOException {
        return doMethodJson(urlString, "PUT", csrf, body, responseClass);
    }

    // Делаем запрос, передавая данные как multipart/form-data
    public <R> R doMethodFormData(String urlString,
                                  String method,
                                  String body,
                                  Class<R> responseClass
    ) throws IOException {
        return doMethod(
                urlString,
                method,
                null,
                FORM_DATA,
                body,
                () -> body,
                responseClass
        );
    }

    // Делаем запрос, передавая данные как application/json
    public <R> R doMethodJson(String urlString,
                              String method,
                              String csrf,
                              Object body,
                              Class<R> responseClass
    ) throws IOException {
        return doMethod(
                urlString,
                method,
                csrf,
                JSON,
                body,
                () -> {
                    try {
                        return objectMapper.writeValueAsString(body);
                    } catch (JsonProcessingException e) {
                        log.error("Проблема с ObjectMapper'ом", e);
                        throw new RuntimeException(e);
                    }
                },
                responseClass
        );
    }

    /**
     * Делаем запрос, передавая данные, если надо, получая данные, если надо, добавляя заголовок, если надо.
     *
     * @param urlString     Ручка, которую дергаем
     * @param method        Метод, который посылаем
     * @param csrf          Значение токена, который будет в заголовке, если есть
     * @param contentType   Значение заголовка Content-Type
     * @param body          Тело, которое отправляем
     * @param bodyToString  Функция преобразования тела в строку
     * @param responseClass Класс, в который мы хотим преобразовать ответ
     * @param <R>           Параметр для возвращаемого объекта
     * @param <B>           Параметр для тела запроса
     * @return Объект типа R (Да, можно получить объект из ответа, но мне оно не надо, оставил для расширения)
     * @throws IOException На все случаи жизни
     */
    private <R, B> R doMethod(String urlString,
                              String method,
                              String csrf,
                              String contentType,
                              B body,
                              Supplier<String> bodyToString,
                              Class<R> responseClass
    ) throws IOException {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Accept", JSON);

            if (csrf != null) {
                connection.setRequestProperty("X-Csrf-Token", csrf);
            }

            if (body != null) {
                connection.setDoOutput(true);
                String json = bodyToString.get();
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                if (responseClass != null) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return objectMapper.readValue(response.toString(), responseClass);
                } else {
                    return null;
                }
            }
        } catch (MalformedURLException e) {
            log.error("Не можем разобрать переданный URL", e);
            throw e;
        } catch (IOException e) {
            log.error("Проблема с чтением/записью", e);
            throw e;
        }
    }
}
