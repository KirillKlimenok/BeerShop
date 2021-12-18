package modsen.com.service.TokenService;

import jakarta.validation.constraints.NotNull;
import modsen.com.service.UnregisteredUserSevice.UnregisteredUserService;

import java.util.Objects;
import java.util.UUID;

public class TokenService {
    public static final String SERVER_SECRET_WORD = "Живи нефильтрованной жизнью";

    public String getToken(@NotNull UnregisteredUserService unregisterUser) {
        int hash = (Objects.hash(unregisterUser.getLogin()) + Objects.hash(unregisterUser.getPassword())) / 2;
        return String.valueOf(hash + SERVER_SECRET_WORD.hashCode() + unregisterUser.hashCode());
    }

}
