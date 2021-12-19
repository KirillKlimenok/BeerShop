package modsen.com.сontroller.login;

import modsen.com.repository.UserRepository.UserRepository;
import modsen.com.service.JsonMapperService.JsonMapperService;
import modsen.com.service.UnregisteredUserSevice.UnregisteredUserService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "LogInServlet", value = "/login")
public class LogInServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UnregisteredUserService user = new JsonMapperService().getObj(getBodyReq(request),UnregisteredUserService.class);
        try {
            String token = new UserRepository().getUserToken(user);
            response.sendRedirect("/" + token);
        } catch (SQLException e) {
            response.sendError(405,"wrong login or password");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

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
