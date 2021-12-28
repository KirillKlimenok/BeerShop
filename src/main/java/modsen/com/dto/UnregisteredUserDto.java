package modsen.com.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import modsen.com.exceptions.NotTrueValidationUserException;
import modsen.com.service.validation.ValidationsService;
import modsen.com.service.validation.ValidationsServiceImpl;

import java.util.Objects;

@Data
public class UnregisteredUserDto {

    private String login;

    private String email;

    private String password;

    public UnregisteredUserDto(@JsonProperty("mail") String mail, @JsonProperty("login") String login, @JsonProperty("password") String password) throws NotTrueValidationUserException {
        ValidationsService validationsService = new ValidationsServiceImpl();
        if(validationsService.isTrueMail(mail) && validationsService.isTrueLogin(login))
        {
            this.email = mail;
            this.login = login;
            this.password = Objects.hash(password)+"";
        }else{
            throw new NotTrueValidationUserException("you enter wrong mail or login. Please your login must consist only of Latin letters");
        }

    }

}
