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
    private String Q  ;
    int start;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            start = Integer.parseInt(request.getParameter("start"));
        }
        catch (NumberFormatException e){
            start=0;
        }

        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        Session session = factory.openSession();

        //String q="from Rencontre r, r.players p where p.firstName like :parti ORDER BY r.dateDebut desc";
        List<Rencontre> rencontresQ  = search(session,request.getParameter("ville"), request.getParameter("organisateur"),
                request.getParameter("date"), request.getParameter("nbJoueur"), request.getParameter("participant"),
                request.getParameter("description"));
        // List<Rencontre> rencontresQ = session.createQuery(q).setParameter("parti",request.getParameter("participant")).setFirstResult(start).setMaxResults(MAX_RESULTS).list();

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

        jsonResponse.put("Q",Q);
        response.setContentType("text/json");
        session.close();
        response.getWriter().print(jsonResponse.toString());
    }

    private List<Rencontre> search(Session session,String ville, String organisateur, String date, String nbJoueur,
                                   String participant, String description) {
        String q="";

        if(ville !=null && !ville.equals("")){
            q+=" r.stade.commune ='"+ville+"'";
        }
        if(organisateur!=null && organisateur!=""){

            if(ville!=null && !ville.equals(""))
                q += " and ";
            q += " (r.organizer.firstName like '%" + organisateur + "%' or r.organizer.lastName like '%" + organisateur + "%'" +
                    " or not r.organizer.email like '%" + organisateur + "%')";
        }
        if(date!=null && date!=""){
            if(organisateur!=null && organisateur!="" ||ville!=null && !ville.equals("")  )
                q += " and ";
            q+=" DATE_FORMAT(r.dateDebut,'%Y-%m-%d')='"+date+"'";
        }
        if(nbJoueur!=null && nbJoueur!="" ){
            if(date!=null && date!="" || organisateur!=null && organisateur!="" ||ville!=null && !ville.equals("")  )
                q += " and ";
            q+=" r.nbJoueurs="+Integer.parseInt(nbJoueur);
        }
        if(description!=null && description!="" ){
            if(nbJoueur!=null && nbJoueur!="" || date!=null && date!="" || organisateur!=null && organisateur!="" ||ville!=null && !ville.equals("") )
                q += " and ";
            q+=" r.description like '%"+description+"%'";
        }

        if(!q.equals(""))
            q="from Rencontre r where "+q;
        else
            q="from Rencontre r";

        q+=" order by r.dateDebut desc";

        Q=q;
        List<Rencontre> rencontres=new ArrayList<>();

        rencontres = session.createQuery(q).setFirstResult(start).setMaxResults(MAX_RESULTS).list();

        return rencontres;
    }
/*
    private List<Rencontre> searchOld(Session session,String ville, String organisateur, String date, String nbJoueur,
                                      String participant, String description) {

        List rencontres;

        String q="from Rencontre r ORDER BY r.dateDebut desc";
        rencontres = session.createQuery(q).setFirstResult(start).setMaxResults(MAX_RESULTS).list();

        if(ville!=null ){
            rencontres.removeAll((session.createQuery("from Rencontre r where r.stade.commune<>:ville").setParameter("ville", ville)
                    .setFirstResult(start).setMaxResults(MAX_RESULTS).list()));
        }
        if(organisateur!=null){
            rencontres.removeAll(session.createQuery("from Rencontre r where (r.organizer.firstName not like :organisateur and " +
                    "r.organizer.lastName not like :organisateur and not r.organizer.email  not like :organisateur)").
                    setParameter("organisateur", organisateur).
                    setFirstResult(start).setMaxResults(MAX_RESULTS).list());
        }
        if(date!=null){
            rencontres.removeAll(session.createQuery("from Rencontre r where DATE_FORMAT(r.dateDebut,'%Y-%m-%d')<>:date").setParameter("date",date).list());
        }
        if(nbJoueur!=null){
            rencontres.removeAll(session.createQuery("from Rencontre r where r.nbJoueurs<>:nbJoueur").setParameter("nbJoueur",Integer.parseInt(nbJoueur)).list());
        }
        if(description!=null){
            rencontres.removeAll(session.createQuery("from Rencontre r,  r.players p where r.description not like :descr and p.email like 'issa'").setParameter("descr",description).list());
        }
        if(participant!=null){
            List<Rencontre> rencontres1 = new ArrayList<>(rencontres);
            for(Object o : rencontres){
                Rencontre r = (Rencontre)o;
                for(User u : r.getPlayers()){
                    if(!u.getEmail().matches(participant) && !u.getFirstName().matches(participant) && !u.getLastName().matches(participant))
                        rencontres1.remove(o);
                }
            }

            rencontres = rencontres1;
        }
//        else if(date!=null) {
//            User parti=(User)session.createQuery("from User where email like :particip or lastName like :particip or firstName like :particip").setParameter("particip",participant);
//
//            rencontres = session.createQuery("from Rencontre r where r.stade.commune=:ville and" +
//                    " (r.organizer.firstName like :organisateur or r.organizer.lastName like :organisateur or r.organizer.email like :organisateur) and " +
//                    " r.nbJoueurs=:nbJoueuer").setParameter("ville", vi   lle).setParameter("nbJoueuer", Integer.parseInt(nbJoueur)).setFirstResult(start).setMaxResults(MAX_RESULTS).list();
//                    /*and parti in r.players*/
//        }

    //return rencontres;

//}

}
