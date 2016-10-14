package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import init.Init;
import models.Rencontre;
import models.Stade;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khelifa on 10/10/2016.
 */
public class ListeRencontre extends HttpServlet {

    public static final int MAX_RESULTS = 5;
    int start;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {




        try {
            start = Integer.parseInt(request.getParameter("start"));
        }
        catch (NumberFormatException e){
            start=0;
        }

        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        Session session = factory.openSession();

        String q="from Rencontre r ORDER BY r.dateDebut desc";
        List<Rencontre> rencontresQ  = search(session,request.getParameter("ville"), request.getParameter("organisateur"),
                request.getParameter("date"), request.getParameter("nbJoueur"), request.getParameter("participant"),
                request.getParameter("description"));
        //rencontresQ = session.createQuery(q).setFirstResult(start).setMaxResults(MAX_RESULTS).list();

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy à HH:mm");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResponse = mapper.createObjectNode();
        ArrayNode rencontres = jsonResponse.putArray("rencontres");

        for(Rencontre r : rencontresQ){
            ObjectNode rencontre = rencontres.addObject();
            ObjectNode organizateur = rencontre.putObject("organisateur");

            organizateur.put("id",r.getOrganizer().getId());
            organizateur.put("nom",r.getOrganizer().getFirstName() );
            organizateur.put("prenom",r.getOrganizer().getLastName() );
            organizateur.put("img",r.getOrganizer().getImg());

            rencontre.put("id",r.getId());
            rencontre.put("nbJoueurCurr",r.getPlayers().size());
            rencontre.put("nbJoueurTotal", r.getNbJoueurs());
            rencontre.put("dateHeure","Le "+df.format(r.getDateDebut()));
            rencontre.put("nomStade",r.getStade().getNom());
            rencontre.put("description",r.getDescription());
            rencontre.put("ville",r.getStade().getCommune()+" "+r.getStade().getCodePostal());
        }
        response.setContentType("text/json");
        response.getWriter().print(jsonResponse.toString());
    }

    private List<Rencontre> search(Session session,String ville, String organisateur, String date, String nbJoueur, String participant, String description) {

        List rencontres= new ArrayList<>();
       // if(ville==null && organisateur==null && date==null && nbJoueur==null && participant==null && description==null){
            String q="from Rencontre r ORDER BY r.dateDebut desc";
            rencontres = session.createQuery(q).setFirstResult(start).setMaxResults(MAX_RESULTS).list();
        //}
//        else if(){
//
//        }
//        else if(ville!=null) {
//            User parti=(User)session.createQuery("from User where email like :particip or lastName like :particip or firstName like :particip").setParameter("particip",participant);
//
//            rencontres = session.createQuery("from Rencontre r where r.stade.commune=:ville and" +
//                    " (r.organizer.firstName like :organisateur or r.organizer.lastName like :organisateur or r.organizer.email like :organisateur) and " +
//                    " r.nbJoueurs=:nbJoueuer").setParameter("ville", ville).setParameter("nbJoueuer", Integer.parseInt(nbJoueur)).setFirstResult(start).setMaxResults(MAX_RESULTS).list();
//                    /*and parti in r.players*/
//        }

        return rencontres;
    }
}
