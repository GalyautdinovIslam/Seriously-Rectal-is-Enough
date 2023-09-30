package ru.itis.oncall.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.itis.oncall.models.Team;

@Data
public class TeamRequest {
    @JsonProperty("name")
    private String name;
    @JsonProperty("scheduling_timezone")
    private String schedulingTimezone;
    @JsonProperty("email")
    private String email;
    @JsonProperty("slack_channel")
    private String slackChannel;

    public TeamRequest(Team team) {
        this.name = team.getName();
        this.schedulingTimezone = team.getSchedulingTimezone();
        this.email = team.getEmail();
        this.slackChannel = team.getSlackChannel();
    }
}
