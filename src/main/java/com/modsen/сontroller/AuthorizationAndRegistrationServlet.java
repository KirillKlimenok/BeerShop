package com.modsen.сontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.modsen.dto.UnregisteredUserDto;
import com.modsen.exceptions.NotTrueValidationUserException;
import com.modsen.repository.user.UserRepository;
import com.modsen.service.jsonmapper.JsonMapperServiceImpl;
import com.modsen.service.user.UserService;
import com.modsen.service.validation.EmailValidatorService;
import com.modsen.service.validation.LoginValidatorService;
import com.modsen.service.validation.ValidationsService;
import com.modsen.service.validation.ValidationsServiceImpl;
import com.modsen.service.validation.Validator;
import lombok.extern.log4j.Log4j;
import com.modsen.service.user.UserServiceImpl;
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
        validationsService = new ValidationsServiceImpl();
        userService = new UserServiceImpl(new UserRepository(), validationsService);
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
