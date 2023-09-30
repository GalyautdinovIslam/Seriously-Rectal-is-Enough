package ru.itis.oncall.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.itis.oncall.models.WithName;

@Data
public class OnlyNameRequest {
    @JsonProperty("name")
    private String name;

    public OnlyNameRequest(WithName withName) {
        this.name = withName.getName();
    }
}
