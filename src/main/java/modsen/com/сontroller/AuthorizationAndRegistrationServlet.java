package modsen.com.сontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import modsen.com.repository.user.UserRepository;
import modsen.com.service.jsonmapper.JsonMapperServiceImpl;
import modsen.com.dto.UnregisteredUserDto;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AuthorizationAndRegistrationServlet", value = "/auth")
public class AuthorizationAndRegistrationServlet extends HttpServlet {
    JsonMapperServiceImpl jsonMapperService;
    UserRepository userRepository;


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UnregisteredUserDto user = jsonMapperService.getObj(getBodyReq(request), UnregisteredUserDto.class);
        String token = userRepository.getUserToken(user);
        response.sendRedirect("/" + token);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jsonUnregUser = getBodyReq(request);
        try {
            UnregisteredUserDto user = jsonMapperService.getObj(jsonUnregUser, UnregisteredUserDto.class);
            userRepository.writeUser(user);
            response.setStatus(200);
        } catch (SQLException | JsonProcessingException e) {
            response.sendError(400, "you entered wrong login or password\n" + e.getMessage());
        }

    }

    @Override
    public void init() {
        jsonMapperService = new JsonMapperServiceImpl();
        userRepository = new UserRepository();
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
