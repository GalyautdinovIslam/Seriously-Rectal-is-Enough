package ru.itis.exporter.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private Integer id;
    private Long start;
    private Long end;
    private String role;
    private String team;
    private String user;
}
