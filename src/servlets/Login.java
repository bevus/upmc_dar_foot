package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import forms.SingUpForm;
import init.Init;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import utils.HelperFunctions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Hacene on 08/10/2016.
 */
public class Login extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sessionFactory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        ObjectMapper mapper = new ObjectMapper();
        User user = new User();
        user.setEmail(Form.getField("login_email", request));
        user.setPassword(HelperFunctions.getSHA1(Form.getField("login_password", request)));
        user = Form.checkLogin(user.getEmail(), user.getPassword(), sessionFactory.openSession());
        ObjectNode jsonRespons = mapper.createObjectNode();
        if(user != null){
            request.getSession().setAttribute("user", user);
            jsonRespons.put("ok", true);
        }else{
            jsonRespons.put("ok", false);
        }
        response.getWriter().print(jsonRespons.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
