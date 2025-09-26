package com.lshdainty.porest.login.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.porest.user.domain.User;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginDto {
    private String id;
    private String pw;
    private String name;
    private String email;
    private String provider;
    private String providerId;

    public LoginDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.provider = "";
        this.providerId = "";
    }
}
