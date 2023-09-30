package ru.itis.oncall.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.itis.oncall.models.Duty;
import ru.itis.oncall.models.Team;
import ru.itis.oncall.models.User;
import ru.itis.oncall.utils.DutyDateParser;

import java.text.ParseException;

@Data
public class DutyRequest {
    @JsonProperty("start")
    private Long start;
    @JsonProperty("end")
    private Long end;
    @JsonProperty("user")
    private String user;
    @JsonProperty("team")
    private String team;
    @JsonProperty("role")
    private String role;


    public DutyRequest(Team team, User user, Duty duty) throws ParseException {
        this.start = DutyDateParser.parse(duty.getDate());
        this.end = this.start + 86_400;
        this.user = user.getName();
        this.team = team.getName();
        this.role = duty.getRole();
    }
}
