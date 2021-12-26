package modsen.com.service.token;

import jakarta.validation.constraints.NotNull;
import modsen.com.dto.UnregisteredUserDto;

public interface TokenService {
    String getToken(@NotNull UnregisteredUserDto unregisterUser);
}
