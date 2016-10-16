package servlets;

import models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Hacene on 07/10/2016.
 */

/**
 * Test sur le serveur
 */
public class Test extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        SessionFactory factory = (SessionFactory) getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
//        Session session = factory.openSession();
//        Transaction transaction = session.beginTransaction();
//
//        User user = new User();
//        user.setEmail("sdfsq@sf.fr");
//        user.setFirstName("hacene");
//        user.setLastName("kedjar");
//        user.setPassword("123456");
//
//        session.save(user);
//        transaction.commit();
//populate(sessionFactory.openSession(),new Date());


        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

        Date now = new Date();

        for(int i =1; i< 11; i++){
            populate(sessionFactory.openSession(),now);
            now.setTime(1000*3600*24*i+now.getTime());
        }
    }
    public static void populate(Session session, Date date){
        // addresses
        Address addressHacene = new Address();
        addressHacene.setCity("Villetaneuse");
        addressHacene.setNumber(21);
        addressHacene.setStreet("Marguerite Yourcenar");
        addressHacene.setPosteCode("93430");

        Address addressZahir = new Address();
        addressZahir.setCity("Paris");
        addressZahir.setNumber(75);
        addressZahir.setStreet("Charle Michel");
        addressZahir.setPosteCode("75015");

        Address addressKhelifa = new Address();
        addressKhelifa.setCity("Epinay");
        addressKhelifa.setNumber(75);
        addressKhelifa.setStreet("Paris");
        addressKhelifa.setPosteCode("93800");

        // players
        User hacene = new User();
        hacene.setAddress(addressHacene);
        hacene.setFirstName("Hacene");
        hacene.setLastName("Kedjar");
        hacene.setEmail("hacene.kedjar@gmail.com");
        hacene.setImg("1466032685.png");
        hacene.setCreationDate(new Date());

        User zahir = new User();
        zahir.setAddress(addressZahir);
        zahir.setFirstName("Zahir");
        zahir.setLastName("Chelbi");
        zahir.setEmail("zahir.chelbi@gmail.com");
        zahir.setImg("1466032719.png");
        zahir.setCreationDate(new Date());

        User khelifa = new User();
        khelifa.setAddress(addressKhelifa);
        khelifa.setFirstName("Khelifa");
        khelifa.setLastName("Berrfas");
        khelifa.setEmail("khelifa.berffas.gmail.com");
        khelifa.setImg("1466356520.png");
        khelifa.setCreationDate(new Date());
        //stade
        Stade stade = new Stade();
        stade.setCodePostal(78057);
        stade.setCommune("Bennecourt");
        stade.setLatitude(49.03841000092038);
        stade.setLongitude(1.57235);
        stade.setNom("STADE AURÉLIEN BAZIN");
        stade.setNote(0);
        //comments
        Comment comment1 = new Comment();
        comment1.setStade(stade);
        comment1.setAuthor(hacene);
        comment1.setDateComment(new Date());
        comment1.setTextComment("Never hoist a bucaneer.All freebooters desire coal-black, stormy yardarms.");

        Comment comment2 = new Comment();
        comment2.setStade(stade);
        comment2.setAuthor(zahir);
        comment2.setDateComment(new Date());
        comment2.setTextComment("Never hoist a bucaneer.All freebooters desire coal-black, stormy yardarms.");

        Comment comment3 = new Comment();
        comment3.setStade(stade);
        comment3.setAuthor(khelifa);
        comment3.setDateComment(new Date());
        comment3.setTextComment("Never hoist a bucaneer.All freebooters desire coal-black, stormy yardarms.");
        // match
        Rencontre rencontre = new Rencontre();
        rencontre.setDateDebut(date);
        rencontre.setNbJoueurs(10);
        rencontre.setOrganizer(khelifa);

        rencontre.setStade(stade);
        rencontre.setPlayers(new ArrayList<>());
//        rencontre.getPlayers().add(hacene);
//        rencontre.getPlayers().add(zahir);
//        rencontre.getPlayers().add(khelifa);

        session.beginTransaction();
        session.save(comment1);
        session.save(comment2);
        session.save(comment3);
        session.save(rencontre);
        session.getTransaction().commit();
        session.close();
    }

}
