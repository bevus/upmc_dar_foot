package init;

import models.Meteo;
import models.Rencontre;
import models.RencontreUser;
import utils.HelperFunctions;

import java.util.Observable;


/**
 * Created by khelifa on 05/11/2016.
 */
public class NotifyUsers implements Observer {
    @Override
    public void weatherAvailable(Rencontre rencontre , Meteo meteo, Meteo newMeteo) {

        String subject = "Darfoot Meteo disponible !";
        String body = "Bonjour,\n" +
                "La meteo est disponible pour la rencontre du "+ rencontre.getDateDebut() +" au stade " +
                rencontre.getStade().getNom()+ " à "+rencontre.getStade().getCommune()+" Meteo :Temperature "+newMeteo.getDayT()+"° C\n"+
                "Temps "+newMeteo.getDescription()+ "\n"+
                "Cliquez <a href='"+Consts.WEB_ROOT+"/rencontre.html?id="+rencontre.getId()+"'> ici </a> pour accedez la page de la rencontre"
                ;

        for(RencontreUser r : rencontre.getPlayers()){
            String mailTo = r.getPlayer().getEmail();

            HelperFunctions.sendMail(mailTo,subject,body);
        }
    }

    @Override
    public void weatherChanged(Rencontre rencontre, Meteo meteo, Meteo newMeteo) {
        String subject = "Darfoot changement de la meteo";

        String body = "Bonjour,\n" +
                "La meteo pour la rencontre du "+ rencontre.getDateDebut() +" au stade " +
                rencontre.getStade().getNom()+ " à "+rencontre.getStade().getCommune()+" à changer \n Meteo :Temperature "+newMeteo.getDayT()+"° C\n"+
                "Temps "+newMeteo.getDescription()+"\n"+
                "Cliquez <a href='"+Consts.WEB_ROOT+"/rencontre.html?id="+rencontre.getId()+"'> ici </a> pour accedez la page de la rencontre"
                ;

        for(RencontreUser r : rencontre.getPlayers()){
            String mailTo = r.getPlayer().getEmail();

            HelperFunctions.sendMail(mailTo,subject,body);
        }
    }
}
