package forms;

import models.Rencontre;
import models.RencontreUser;
import models.Stade;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Hacene on 17/10/2016.
 */
public class ScheduleGameForm extends Form {
    public ScheduleGameForm(SessionFactory factory) {
        super(factory);
    }

    public Rencontre validate(HttpServletRequest request, User user){
        Session session = factory.openSession();
        Rencontre rencontre = new Rencontre();
        Stade stade = null;
        try{
            stade = checkStade(getField("stadeId", request), session);
            rencontre.setStade(stade);
        }catch (Exception e){
            error.put("stadeId", e.getMessage());
        }
        try{
            rencontre.setNbJoueurs(checkNbPlayers(getField("nbJoueurs", request)));
        }catch (Exception e){
            error.put("nbJoueurs", e.getMessage());
        }
        try{
            rencontre.setDescription(checkGameInformation(getField("description", request)));
        }catch (Exception e){
            error.put("description", e.getMessage());
        }
        try{
            rencontre.setDateDebut(checkGameDate(getField("gameDate", request), stade, user, session));
        }catch (Exception e){
            error.put("gameDate", e.getMessage());
        }
        if(error.isEmpty()){
            rencontre.setOrganizer(user);
            RencontreUser rencontreUser = new RencontreUser();
            rencontreUser.setRencontre(rencontre);
            rencontreUser.setDateCreation(new Date());
            rencontreUser.setPlayer(user);
            rencontreUser.setTeam(TEAM_A);
            rencontre.setPlayers(new ArrayList<>());
            rencontre.getPlayers().add(rencontreUser);
            session.beginTransaction();
            session.save(rencontre);
            session.getTransaction().commit();
            session.close();
            return rencontre;
        }else{
            return null;
        }
    }
}