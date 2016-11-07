package forms;

import models.Rencontre;
import models.RencontreUser;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by Hacene on 16/10/2016.
 */
public class JoinGameForm extends Form {
    public JoinGameForm(SessionFactory factory) {
        super(factory);
    }

    public RencontreUser validate(HttpServletRequest request, User user){
        Session session = factory.openSession();
        RencontreUser rencontreUser = new RencontreUser();
        String rencontreId = getField("rencontreId", request);

        if(rencontreId != null){
            rencontreUser.setRencontre(session.get(Rencontre.class, Integer.parseInt(rencontreId)));
        }else{
            error.put("rencontreId", "pas de rencontre");
        }

        rencontreUser.setDateCreation(new Date());
        rencontreUser.setPlayer(user);
        rencontreUser.setTeam(getField("team", request));

        try {
            canPlay(rencontreUser.getRencontre(), rencontreUser.getPlayer(), rencontreUser.getTeam());
        }catch (Exception e){
            error.put("user", e.getMessage());
        }
        if(error.isEmpty()){
            session.beginTransaction();
            session.save(rencontreUser);
            session.getTransaction().commit();
            session.close();
            return rencontreUser;
        }else{
            session.close();
            return null;
        }
    }
}
