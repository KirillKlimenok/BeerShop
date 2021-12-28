package modsen.com.service.validation;

import java.util.regex.Pattern;

public class ValidationsServiceImpl implements ValidationsService {
    @Override
    public boolean isTrueLogin(String login) {
        return Pattern.matches("[a-zA-Z]*",login);
    }

    @Override
    public boolean isTrueMail(String mail) {
        String emailRegex = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$";
        return Pattern.matches(emailRegex,mail);
    }
}
