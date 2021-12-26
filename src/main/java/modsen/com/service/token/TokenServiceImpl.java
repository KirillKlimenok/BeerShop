package modsen.com.service.token;

import jakarta.validation.constraints.NotNull;
import modsen.com.dto.UnregisteredUserDto;

import java.util.Objects;

public class TokenServiceImpl implements TokenService{
    private static final String SERVER_SECRET_WORD = "Живи нефильтрованной жизнью";

    @Override
    public String getToken(@NotNull UnregisteredUserDto unregisterUser) {
        int hash = (Objects.hash(unregisterUser.getLogin()) + Objects.hash(unregisterUser.getPassword())) / 2;
        return String.valueOf(hash + SERVER_SECRET_WORD.hashCode() + unregisterUser.hashCode());
    }

}
