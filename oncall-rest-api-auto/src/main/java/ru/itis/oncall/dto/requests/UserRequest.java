package ru.itis.oncall.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.oncall.models.User;

@Data
public class UserRequest {
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("contacts")
    private Contacts contacts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Contacts {
        @JsonProperty("call")
        private String call;
        @JsonProperty("sms")
        private String sms;
        @JsonProperty("email")
        private String email;
    }

    public UserRequest(User user) {
        this.fullName = user.getFullName();
        this.contacts = new Contacts(user.getPhoneNumber(), user.getPhoneNumber(), user.getEmail());
    }
}
