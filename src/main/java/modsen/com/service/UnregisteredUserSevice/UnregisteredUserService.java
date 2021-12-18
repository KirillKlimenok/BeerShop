package modsen.com.service.UnregisteredUserSevice;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class UnregisteredUserService {
    @Getter
    @Setter
    private String login;

    @Getter
    @Setter
    private String password;
}
