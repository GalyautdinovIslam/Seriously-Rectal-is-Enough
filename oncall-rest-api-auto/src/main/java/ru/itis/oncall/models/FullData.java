package ru.itis.oncall.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FullData {
    @JsonProperty("teams")
    private List<Team> teams;
}
