package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import init.Consts;
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
import java.util.concurrent.ExecutionException;

/**
 * Created by Hacene on 02/11/2016.
 */
public class CancelParticipation extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(Consts.CONTENT_TYPE_JSON);
        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        Session session = factory.openSession();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResponse = mapper.createObjectNode();
        try{
            User user = (User)request.getSession().getAttribute(Consts.USER);
            if (user != null) {
                int rencontreId = Integer.parseInt(Form.getField("rencontreId", request));
                Rencontre rencontre = session.get(Rencontre.class, rencontreId);
                if(rencontre != null){
                    if(rencontre.isCancled())
                        throw new Exception("cette rencontre à étée annulé par son organisateur");
                    if(rencontre.getDateDebut().getTime() - Form.NEXT_GAME_MIN_TIME < System.currentTimeMillis())
                        throw new Exception("cette rencotre s'est déja déroulée");
                    if(rencontre.getOrganizer().getId() != user.getId()){
                        RencontreUser rencontreUser = (RencontreUser)session.createQuery("from RencontreUser where rencontre.id = :renconteId and player.id = :playerId")
                                .setParameter("renconteId", rencontreId)
                                .setParameter("playerId", user.getId())
                                .getSingleResult();
                        if(rencontreUser != null){
                            session.beginTransaction();
                            session.delete(rencontreUser);
                            session.getTransaction().commit();
                            session.close();
                            jsonResponse.put("ok", true);
                        }else {
                            throw new Exception("vous n'êtes pas enregistré comme participant à cette rencontre");
                        }
                    }else {
                        throw new Exception("vous êtes l'organisateur de cette rencontre vous ne pouvez pas annulez");
                    }
                }else {
                    throw new Exception("rencontre inconnu");
                }
            }else{
               throw new Exception("vous devez etre connecté pour pouvoir effectuer cette opération");
            }
        }catch (NumberFormatException e){
            jsonResponse.put("error", "rencontre inconnu");
        }catch (Exception e){
            jsonResponse.put("error", e.getMessage());
        }
        response.getWriter().print(jsonResponse.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
