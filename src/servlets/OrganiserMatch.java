package servlets;

import models.Rencontre;
import models.Stade;
import models.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by khelifa on 08/10/2016.
 */
public class OrganiserMatch extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();

       //request.getSession().setAttribute("user",session.get(User.class,9));

        User organisateur = (User) request.getSession().getAttribute("user");

       //int userId = Integer.parseInt(request.getParameter("userId"));
       // int userId = Integer.parseInt(request.getParameter("userId"));
        int stadeId = Integer.parseInt(request.getParameter("stadeId"));
        int nbJoueurs = Integer.parseInt(request.getParameter("nbJoueurs"));

        String startDateString = request.getParameter("date");
        String startTimeString = request.getParameter("time");
        String description = request.getParameter("descrption");
        startDateString = startDateString.replace('/','-');
        startTimeString = startTimeString+":00";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        Date date=null;
        String error="";

        try {

            date = df.parse(startDateString+"T"+startTimeString+".235-0700");
            String newDateString = df.format(date);
//            System.out.println(newDateString);
        } catch (ParseException e) {
            error+=e.getMessage();
            e.printStackTrace();
        }

        Transaction tx = null;

        try{
            tx = session.beginTransaction();

            Rencontre rencontre = new Rencontre();
            //User organisateur = (User)session.createQuery("FROM User where id=:id").setParameter("id",userId).uniqueResult();
            Stade stade = (Stade)session.createQuery("from Stade where id=:id").setParameter("id",stadeId).uniqueResult();
            rencontre.setStade(stade);
            rencontre.setOrganizer(organisateur);
            rencontre.setDateDebut(date);
            rencontre.setNbJoueurs(nbJoueurs);
            List<User> players = new ArrayList<>();
            players.add(organisateur);
            rencontre.setPlayers(players);
            rencontre.setDescription(description);
            session.save(rencontre);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }

        response.getWriter().println("date ajouter ="+date + " date envoyée="+startDateString+"T"+startTimeString+".235-0700"+ " error="+error);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


    }
}
