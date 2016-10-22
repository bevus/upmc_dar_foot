package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import init.Init;
import models.Stade;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by khelifa on 15/10/2016.
 */
public class Stades extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        Session session = factory.openSession();
        List<Stade> stadesList = session.createQuery("from Stade").list();
        ArrayNode json = mapper.createArrayNode();
        for (Stade s : stadesList){
            ObjectNode stade = json.addObject();
            stade.put("id", s.getId());
            stade.put("lat", s.getLatitude());
            stade.put("lon", s.getLongitude());
            stade.put("name", s.getNom());
            stade.put("commune", s.getCommune());
            stade.put("zipCode", s.getCodePostal());
        }
        response.setContentType("text/json");
        response.getWriter().print(json.toString());
        session.close();
    }
}
