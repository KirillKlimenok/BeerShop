package modsen.com.service.UnregisteredUserSevice;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class UnregisteredUserService {
    @Getter
    @Setter
    private String login;

    @Getter
    @Setter
    private String password;

    public UnregisteredUserService(String login, String password) {
        this.login = login;
        this.password = Objects.hash(password)+"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnregisteredUserService that = (UnregisteredUserService) o;
        return Objects.equals(login, that.login) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password);
    }
}
