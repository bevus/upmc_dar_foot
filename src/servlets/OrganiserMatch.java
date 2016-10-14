package servlets;

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
import java.util.Date;

/**
 * Created by khelifa on 08/10/2016.
 */
public class OrganiserMatch extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* A revoir */
        String emailOrga = request.getParameter("emailOrga");
        int stadeId = Integer.parseInt(request.getParameter("stadId"));
        int nbJoueurs = Integer.parseInt(request.getParameter("nbJoueurs"));

        String startDateString = request.getParameter("date");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date=null;
        Date time=null;
        try {
            SimpleDateFormat dt = new SimpleDateFormat("hh:mm a");
            time = dt.parse(request.getParameter("time"));

            date = df.parse(startDateString);
            String newDateString = df.format(date);
            System.out.println(newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();
        Transaction tx = null;

//        try{
//            tx = session.beginTransaction();
//
//            Match match = new Match();
//            User organisateur = (User)session.createQuery("FROM User where email=:emailOrga");
//            match.setOranisateur(organisateur);
//            match.setDate(date);
//            match.setTime(time);
//            match.setNbJoueurs(nbJoueurs);
//
//            session.save(match);
//            tx.commit();
//        }catch (HibernateException e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        }finally {
//            session.close();
//        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            doPost(request,response);
    }
}
