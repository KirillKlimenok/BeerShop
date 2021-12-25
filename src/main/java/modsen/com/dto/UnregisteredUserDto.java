package modsen.com.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class UnregisteredUserDto {

    private String login;

    private String password;

    public UnregisteredUserDto(@JsonProperty("login") String login, @JsonProperty("password") String password) {
        this.login = login;
        this.password = Objects.hash(password)+"";
    }

}
