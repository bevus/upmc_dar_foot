package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by khelifa on 15/10/2016.
 */
public class UserConnected extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        ObjectMapper om = new ObjectMapper();
        response.setContentType("text/json; charset=utf-8");
        response.getWriter().print(om.writeValueAsString(user));
    }
}
