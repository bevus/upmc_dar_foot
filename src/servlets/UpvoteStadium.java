package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import init.Consts;
import init.Init;
import models.Stade;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Hacene on 30/10/2016.
 */
public class UpvoteStadium extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(Consts.CONTENT_TYPE_JSON);
        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        User user = (User)request.getSession().getAttribute(Consts.USER);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResponse = mapper.createObjectNode();
        if(user != null){
            Session session = factory.openSession();
            String tmp = Form.getField("stadeId", request);
            if(tmp != null){
                try{
                    int stadeId = Integer.parseInt(tmp);
                    Stade stade = session.get(Stade.class, stadeId);
                    if(stade != null){
                        try{
                            Form.canUpvoteSatde(user, stade);
                            stade.getStadeUpvoters().add(user);
                            session.beginTransaction();
                            session.update(stade);
                            session.getTransaction().commit();
                            session.close();
                            jsonResponse.put("ok", true);
                            response.getWriter().print(jsonResponse.toString());
                            return;
                        }catch (Exception ignored){
                        }
                    }
                }catch (NumberFormatException ignored){
                }
            }
        }
        jsonResponse.put("error", true);
        response.getWriter().print(jsonResponse.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
