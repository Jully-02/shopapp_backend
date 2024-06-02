package com.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("user")
    private User user;
}
