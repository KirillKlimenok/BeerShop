package modsen.com.сontroller.registration;

import modsen.com.repository.UserRepository.UserRepository;
import modsen.com.service.JsonMapperService.JsonMapperService;
import modsen.com.service.UnregisteredUserSevice.UnregisteredUserService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

@WebServlet(name = "RegistrationServlet", value = "/registration")
public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("enter");
        String jsonUnregUser = getBodyReq(request);
        UnregisteredUserService user = new JsonMapperService().getObj(jsonUnregUser,UnregisteredUserService.class);
        UserRepository userRepository = new UserRepository();
        try {
            userRepository.writeUser(user);
            response.getWriter().write("Done");
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
