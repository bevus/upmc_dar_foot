package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

/**
 * Created by Hacene on 07/10/2016.
 */

public class SingUp extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sessionFactory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        SingUpForm form = new SingUpForm(sessionFactory);
        User user = form.validate(request);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResponse = mapper.createObjectNode();
        if(user != null){
            // create user
            // encrypt password
            user.setPassword(HelperFunctions.getSHA1(user.getPassword()));
            // persist user
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            session.close();

            jsonResponse.put("ok", true);
        }else{
            jsonResponse.put("ok", false);
            ObjectNode errors = mapper.createObjectNode();
            for(String k : form.error.keySet()){
                errors.put(k, form.error.get(k));
            }
            jsonResponse.set("errors", errors);
        }
        response.getWriter().print(jsonResponse.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
