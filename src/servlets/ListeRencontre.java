package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import init.Consts;
import init.Init;
import models.Rencontre;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by khelifa on 10/10/2016.
 */
public class ListeRencontre extends HttpServlet {

    public static final int MAX_RESULTS = 5;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(Consts.CONTENT_TYPE_JSON);

        String keyWord = Form.getField("keyWord", request);
        Date date = null;
        try {
            date = new Date(Long.parseLong(Form.getField("date", request)));
        }catch(Exception ignored){
        }
        Integer nbPlayers = null;
        try{
            nbPlayers = Integer.parseInt(Form.getField("nbPlayers", request));
        }catch (Exception ignored){
        }

        String stringQuery = "from Rencontre where " +
                ((keyWord == null) ? "" : " ( upper(description) like :keyWord or upper(stade.nom) like :keyWord " +
                        "or upper(stade.commune) like :keyWord or upper(organizer.firstName) like :keyWord " +
                        "or upper(organizer.lastName) like :keyWord ) and ") +
                ((date == null) ? "" : " DATE_FORMAT(dateDebut, '%d/%m/%Y') = DATE_FORMAT(:searchDate, '%d/%m/%Y') and ") +
                ((nbPlayers == null) ? "" : " nbJoueurs = :nbJouers and ") + " dateDebut >= :currentDate order by dateDebut asc ";

        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        Session session = factory.openSession();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(stringQuery);
        Query<Rencontre> query = session.createQuery(stringQuery);
        query.setParameter("currentDate", new Date());
        if(keyWord != null){
            query.setParameter("keyWord", "%"+keyWord.toUpperCase()+"%");
        }
        if(date != null){
            query.setParameter("searchDate", date);
        }
        if(nbPlayers != null){
            query.setParameter("nbJouers", nbPlayers);
        }

        List<Rencontre> rencontres = query.list();
        ArrayNode jsonResponse = mapper.createArrayNode();
        for(Rencontre r : rencontres){
            ObjectNode jsonR = jsonResponse.addObject();
            jsonR.put("organizerPic", r.getOrganizer().getImg());
            jsonR.put("organizerFirstName", r.getOrganizer().getFirstName());
            jsonR.put("organizerLastName", r.getOrganizer().getLastName());
            jsonR.put("dateTime", r.getDateDebut().getTime());
            jsonR.put("description", r.getDescription());
            jsonR.put("maxPlayersCount", r.getNbJoueurs() * 2);
            jsonR.put("playersCount", r.getPlayers().size());
            jsonR.put("city", r.getStade().getCommune());
            jsonR.put("id", r.getId());
        }
        session.close();
        response.getWriter().print(jsonResponse.toString());
    }
}