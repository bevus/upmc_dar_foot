package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import init.Consts;
import init.Init;
import models.Meteo;
import models.Rencontre;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Hacene on 01/11/2016.
 */
public class AvailableLocalWeather extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(Consts.CONTENT_TYPE_JSON);
        ObjectMapper mapper = new ObjectMapper();
        try{
            int rencontreId = Integer.parseInt(Form.getField("matchId", request));
            int nbDays = Integer.parseInt(Form.getField("nbDays", request));
            SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
            Session session = factory.openSession();
            Rencontre rencontre = session.get(Rencontre.class, rencontreId);
            if(rencontre != null){
                List<Meteo> meteos = session.createQuery("from Meteo where dayDate >= :currentDate and dayDate <= :dateRencontre and stade.id = :stadeId order by dayDate desc")
                        .setParameter("dateRencontre", rencontre.getDateDebut())
                        .setParameter("stadeId", rencontre.getStade().getId())
                        .setParameter("currentDate", new Date())
                        .setMaxResults(nbDays)
                        .list();
                ArrayNode jsonMeteos = mapper.createArrayNode();
                for(Meteo m : meteos){
                    ObjectNode jsonM = jsonMeteos.addObject();
                    jsonM.put("date", m.getDayDate().getTime());
                    jsonM.put("name", m.getDayName());
                    jsonM.put("dayT", m.getDayT());
                    jsonM.put("nightT", m.getNightT());
                    jsonM.put("icon", m.getIcon());
                    jsonM.put("description", m.getDescription());
                    jsonM.put("humidity", m.getHumidity());
                }
                response.getWriter().print(jsonMeteos);
            }else {
                throw new Exception();
            }
        }catch (Exception e){
            ObjectNode jsonResponse = mapper.createObjectNode();
            e.printStackTrace();
            response.getWriter().print("{'error' : true}");
        }
    }
}
