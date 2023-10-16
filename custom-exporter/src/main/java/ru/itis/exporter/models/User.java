package ru.itis.exporter.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;
    private String name;
    private String fullName;
    private Contacts contacts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Contacts {
        private String email;
        private String sms;
        private String call;
        private String slack;
    }
}
