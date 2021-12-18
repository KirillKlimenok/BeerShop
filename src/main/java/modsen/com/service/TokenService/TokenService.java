package modsen.com.service.TokenService;

import jakarta.validation.constraints.NotNull;
import modsen.com.service.UnregisteredUserSevice.UnregisteredUserService;

import java.util.Objects;

public class TokenService {
    public static final String SERVER_SECRET_WORD = "Живи нефильтрованной жизнью";

    public int getToken(@NotNull UnregisteredUserService unregisterUser){
        return Objects.hash(unregisterUser,SERVER_SECRET_WORD);
    }

}
