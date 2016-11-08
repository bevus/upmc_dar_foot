package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import init.Consts;
import init.Init;
import init.NotifyUsers;
import models.Rencontre;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Hacene on 06/11/2016.
 */
public class CancelGame extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(Consts.CONTENT_TYPE_JSON);
        ObjectMapper mapper = new ObjectMapper();
        User user = (User)request.getSession().getAttribute(Consts.USER);
        ObjectNode jsonResponse = mapper.createObjectNode();
        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        Session session = factory.openSession();
        try{
            if(user == null)
                throw new Exception("vous n'êtes pas connecté");
            int id = Integer.parseInt(Form.getField("id", request));
            Rencontre rencontre = session.get(Rencontre.class, id);
            if(rencontre.getOrganizer().getId() != user.getId())
                throw new Exception("vous n'êtes autorisé à effectuer cette opération");
            if(rencontre.isCancled())
                throw new Exception("cette rencotre est déja annulée");
            if(rencontre.getDateDebut().getTime() - Form.NEXT_GAME_MIN_TIME < System.currentTimeMillis())
                throw new Exception("cette rencotre s'est déja déroulée");
            session.beginTransaction();
            rencontre.setCancled(true);
            session.update(rencontre);
            session.getTransaction().commit();
            jsonResponse.put("ok", true);
            response.getWriter().print(jsonResponse.toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NotifyUsers notifyUsers = new NotifyUsers(getServletContext());
                    notifyUsers.gameCanceled(rencontre);
                    session.close();
                }
            }).start();
            return;
        }catch (NumberFormatException e){
           jsonResponse.put("error", "match incconu");
        }catch (Exception e){
            jsonResponse.put("error", e.getMessage());
        }
        session.close();
        response.getWriter().print(jsonResponse.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
