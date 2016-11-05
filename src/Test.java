import models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import utils.HelperFunctions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Hacene on 08/10/2016.
 */
public class Test {

    public static void main(String[] args) throws Exception {
//        populate();
    }

    public static User addUser(Address addr, String fNmae, String lName, String email, String img){
        User user = new User();
        user.setAddress(addr);
        user.setFirstName(fNmae);
        user.setLastName(lName);
        user.setPassword(HelperFunctions.getSHA1("password"));
        user.setEmail(email);
        user.setImg(img);
        user.setCreationDate(new Date());
        return user;
    }

    public static void populate(){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
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

        List<User> usres = new ArrayList<>();
        usres.add(addUser(addressHacene, "hacene", "kedjar", "hacene.kedjar@gmail.com", "1466032685.png"));
        usres.add(addUser(addressKhelifa, "khelifa", "berrfas", "khelifa.berrfas@gmail.com", "1466032719.png"));
        usres.add(addUser(addressHacene, "zahir", "chelbi", "zahir.chelbi@gmail.com", "1466356520.png"));
        usres.add(addUser(addressHacene, "queen", "hyperspace", "dosi.subspace@gmail.com", "1466356520.png"));
        usres.add(addUser(addressHacene, "mineral", "mystery", "star.city@gmail.com", "1466356520.png"));
        usres.add(addUser(addressHacene, "energy", "Galactic", "energy.galactic@gmail.com", "1466356520.png"));
        // players

        Stade stade = new Stade();
        stade.setCodePostal(78057);
        stade.setCommune("Bennecourt");
        stade.setLatitude(49.03841000092038);
        stade.setLongitude(1.57235);
        stade.setNom("STADE AURÉLIEN BAZIN");
        stade.setNote(0);

        //comments
        Comment comment1 = new Comment();
        ArrayList<User> upvoters = new ArrayList<>();
        upvoters.add(usres.get(0));
        comment1.setUpvoters(upvoters);
        comment1.setStade(stade);
        comment1.setAuthor(usres.get(0));
        comment1.setDateComment(new Date());
        comment1.setTextComment("Never hoist a bucaneer.All freebooters desire coal-black, stormy yardarms.");

        Comment comment2 = new Comment();
        upvoters = new ArrayList<>();
        upvoters.add(usres.get(1));
        comment2.setUpvoters(upvoters);
        comment2.setStade(stade);
        comment2.setAuthor(usres.get(1));
        comment2.setDateComment(new Date());
        comment2.setTextComment("The processor walks mankind like a real green people.Yell wihtout mystery, and we won’t attack a particle.");

        Comment comment3 = new Comment();
        upvoters = new ArrayList<>();
        upvoters.add(usres.get(2));
        comment3.setUpvoters(upvoters);
        comment3.setStade(stade);
        comment3.setAuthor(usres.get(2));
        comment3.setDateComment(new Date());
        comment3.setTextComment("Why does the space suit die?I invade this death, it's called most unusual mineral.Where is the collective crewmate?Planets wobble with moon at the small port!");

        Comment comment4 = new Comment();
        upvoters = new ArrayList<>();
        upvoters.add(usres.get(3));
        comment4.setUpvoters(upvoters);
        comment4.setStade(stade);
        comment4.setAuthor(usres.get(3));
        comment4.setDateComment(new Date());
        comment4.setTextComment("Mineral, paralysis, and pattern.Engage, calm advice!Attitude, vision, and disconnection.Distant tragedies lead to the sonic shower..");

        session.beginTransaction();

        // match
        Date date = HelperFunctions.formatDate(new Date());
        for(int i = 0; i < 5; i++){
            Rencontre rencontre = new Rencontre();
            rencontre.setDescription("Arrr! Pieces o' hunger are forever old.Yuck, undead dubloon. you won't pull the fortress.Arg, heavy-hearted reef.");
            rencontre.setDateDebut(new Date(date.getTime() + i * (3600 * 1000 * 24)));
            rencontre.setNbJoueurs(10);
            rencontre.setOrganizer(usres.get(i % usres.size()));
            rencontre.setStade(stade);

            List<RencontreUser> rencontreUsers = new ArrayList<>();
            int j = 0;
            for (User usre : usres) {
                RencontreUser rencontreUser = new RencontreUser();
                rencontreUser.setDateCreation(date);
                if (j % 2 == 0) {
                    rencontreUser.setTeam("A");
                } else {
                    rencontreUser.setTeam("B");
                }
                j++;
                rencontreUser.setPlayer(usre);
                rencontreUsers.add(rencontreUser);
                rencontreUser.setRencontre(rencontre);
            }
            rencontre.setPlayers(rencontreUsers);
            try {
                rencontre.setMeteo(HelperFunctions.filterMeteo(HelperFunctions.jsonToMeteo(HelperFunctions.getWeatherData(stade.getLatitude()+"", stade.getLongitude()+"", "16"), stade, rencontre), date));
            } catch (Exception ignored) {
            }
            session.persist(rencontre);
        }

        session.persist(comment1);
        session.persist(comment2);
        session.persist(comment3);
        session.persist(comment4);

        session.getTransaction().commit();
        session.close();
    }
}
