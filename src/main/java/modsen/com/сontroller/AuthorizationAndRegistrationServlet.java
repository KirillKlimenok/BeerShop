package modsen.com.сontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j;
import modsen.com.dto.UnregisteredUserDto;
import modsen.com.exceptions.NotTrueValidationUserException;
import modsen.com.service.user.UserService;
import modsen.com.service.user.UserServiceImpl;
import modsen.com.service.jsonmapper.JsonMapperServiceImpl;
import modsen.com.service.validation.EmailValidatorService;
import modsen.com.service.validation.LoginValidatorService;
import modsen.com.service.validation.ValidationsService;
import modsen.com.service.validation.ValidationsServiceImpl;
import modsen.com.service.validation.Validator;
import org.apache.log4j.Priority;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AuthorizationAndRegistrationServlet", value = "/auth")
@Log4j
public class AuthorizationAndRegistrationServlet extends HttpServlet {
    private JsonMapperServiceImpl jsonMapperService;
    private UserService userService;
    private ValidationsService validationsService;
    private List<Validator> validators;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UnregisteredUserDto user = jsonMapperService.getObj(getBodyReq(request), UnregisteredUserDto.class);
        String token = userService.getTokenUser(user);
        response.sendRedirect("/" + token);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jsonUnregUser = getBodyReq(request);
        try {
            UnregisteredUserDto user = jsonMapperService.getObj(jsonUnregUser, UnregisteredUserDto.class);
            if (validationsService.validate(user,validators)) {
                userService.createNewUser(user, validators);
                response.setStatus(200);
            } else {
                response.sendError(400, "wrong email or password");
            }
        } catch (JsonProcessingException | NotTrueValidationUserException e) {
            log.log(Priority.WARN, e.getMessage());
            response.sendError(400, "you entered wrong login or password\n");
        }

    }

    @Override
    public void init() {
        jsonMapperService = new JsonMapperServiceImpl();
        userService = new UserServiceImpl();
        validationsService = new ValidationsServiceImpl();
        validators = new ArrayList<Validator>() {{
            add(new EmailValidatorService());
            add(new LoginValidatorService());
        }};
    }

    private String getBodyReq(HttpServletRequest req) throws IOException {
        BufferedReader bufferedReader = req.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        String str;

        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str).append("\n");
        }
        return stringBuilder.toString();
    }
}
