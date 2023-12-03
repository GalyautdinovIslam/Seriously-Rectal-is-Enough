package ru.itis.slaprober;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import ru.itis.slaprober.interceptors.CookieInterceptor;

@EnableScheduling
@SpringBootApplication
public class SlaProberApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlaProberApplication.class, args);
    }

    // TODO: use configuration class for beans
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
                                     CookieInterceptor cookieInterceptor) {
        return restTemplateBuilder
                .interceptors(cookieInterceptor)
                .build();
    }
}
