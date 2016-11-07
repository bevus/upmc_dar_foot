package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import init.Consts;
import init.Init;
import models.Rencontre;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import utils.HelperFunctions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Hacene on 05/11/2016.
 */
public class MesRencontre extends HttpServlet {

    private static final int GET_USER_UPCOMMING_MATCHS = 0;
    private static final int GET_ORGANIZED_MATCHS = 1;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(Consts.CONTENT_TYPE_JSON);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResponse = mapper.createObjectNode();
        User user = (User)request.getSession().getAttribute(Consts.USER);
        try{
            if(user == null)
                throw new Exception("vous n'étes pas conneté");
            int op = Integer.parseInt(Form.getField("opId", request));
            SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
            Session session = factory.openSession();
            List<Rencontre> rencontres;
            switch (op){
                case GET_USER_UPCOMMING_MATCHS:
                    rencontres = session.createQuery("select r from RencontreUser as ru inner join ru.rencontre as r where ru.player.id = :id and r.dateDebut >= :currentDate and r.cancled = false order by r.dateDebut")
                            .setParameter("id", user.getId())
                            .setParameter("currentDate", new Date()).list();
                    break;
                case GET_ORGANIZED_MATCHS:
                    rencontres = session.createQuery("from Rencontre where organizer.id = :id and dateDebut >= :currentDate and cancled = false ")
                            .setParameter("id", user.getId())
                            .setParameter("currentDate", new Date()).list();
                    break;
                default:
                    throw new Exception("operation invalide");
            }
            ArrayNode jsonR = mapper.createArrayNode();
            for(Rencontre rencontre : rencontres){
                HelperFunctions.fillRencontreObjectNode(jsonR.addObject(), rencontre);
            }
            session.close();
            response.getWriter().print(jsonR.toString());
            return;
        }catch (NumberFormatException e){
            jsonResponse.put("error", "operation invalide");
        } catch (Exception e){
            e.printStackTrace();
            jsonResponse.put("error", e.getMessage());
        }
        response.getWriter().print(jsonResponse.toString());
    }
}
