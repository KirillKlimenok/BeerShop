package modsen.com.сontroller;

import modsen.com.repository.UserRepository.UserRepository;
import modsen.com.service.JsonMapperService.JsonMapperService;
import modsen.com.dto.UnregisteredUserDto;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AuthorizationAndRegistrationServlet", value = "/auth")
public class AuthorizationAndRegistrationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UnregisteredUserDto user = new JsonMapperService().getObj(getBodyReq(request), UnregisteredUserDto.class);
        String token = new UserRepository().getUserToken(user);
        response.sendRedirect("/" + token);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jsonUnregUser = getBodyReq(request);
        UnregisteredUserDto user = new JsonMapperService().getObj(jsonUnregUser, UnregisteredUserDto.class);
        UserRepository userRepository = new UserRepository();
        try {
            userRepository.writeUser(user);
            response.setStatus(200);
        } catch (SQLException e) {
            response.sendError(405,"you entered wrong login or password\n" + e.getMessage());
        }

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
