package ru.itis.oncall.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Duty {
    @JsonProperty("date")
    private String date;
    @JsonProperty("role")
    private String role;
}
