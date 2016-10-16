package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.JoinGameForm;
import init.Init;
import models.Rencontre;
import models.RencontreUser;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Hacene on 13/10/2016.
 */
public class DetailRencontre extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        ObjectMapper mapper = new ObjectMapper();
        User user = (User)request.getSession().getAttribute("user");
        JoinGameForm form = new JoinGameForm(factory);
        RencontreUser rencontreUser = form.validate(request, user);

        response.setContentType("text/json");
        if(rencontreUser != null){
            response.getWriter().print(mapper.writeValueAsString(rencontreUser));
        }else{
            ObjectNode jsonResponse = mapper.createObjectNode();
            jsonResponse.put("error", true);
            for(String key : form.error.keySet()){
                jsonResponse.put(key, form.error.get(key));
            }
            response.getWriter().print(jsonResponse.toString());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        Session session = factory.openSession();
        Rencontre rencontre = session.get(Rencontre.class, 1);
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("text/json");
        session.close();
        response.getWriter().print(mapper.writeValueAsString(rencontre));
        session.close();
    }
}
