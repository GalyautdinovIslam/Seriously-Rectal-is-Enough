package ru.itis.oncall.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Team implements WithName {
    @JsonProperty("name")
    private String name;
    @JsonProperty("scheduling_timezone")
    private String schedulingTimezone;
    @JsonProperty("email")
    private String email;
    @JsonProperty("slack_channel")
    private String slackChannel;
    @JsonProperty("users")
    private List<User> users;
}
