package init;

import models.Meteo;
import models.Rencontre;
import models.RencontreUser;
import utils.HelperFunctions;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;


/**
 * Created by khelifa on 05/11/2016.
 */
@SuppressWarnings("Duplicates")
public class NotifyUsers implements Observer {
    public NotifyUsers(ServletContext servletContext) {
        context = servletContext;
    }

    private ServletContext context;

    private String getTemplateContent(BufferedReader reader, Map<String, String> args) throws IOException {
        String mailBody = "";
        mailBody = HelperFunctions.getContent(reader);
        for(String k : args.keySet()){
            mailBody = mailBody.replace(k, args.get(k));
        }
        return mailBody;
    }
    @Override
    public void weatherAvailable(Rencontre rencontre , Meteo meteo, Meteo newMeteo) {

        String subject = "Darfoot Meteo disponible !";
        for(RencontreUser player : rencontre.getPlayers()){
            String mailTo = player.getPlayer().getEmail();
            String mailBody = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("Ressources/templates/WeatherAvalable.xhtml"))))) {
                Map<String, String> args = new HashMap<>();
                args.put("__firstName__", player.getPlayer().getFirstName());
                args.put("__MatchDate__", rencontre.getDateDebut().toString());
                args.put("__satdeName__", rencontre.getStade().getNom());
                args.put("__link__", Consts.WEB_ROOT + "/rencontre.html?id=" + rencontre.getId());
                mailBody = getTemplateContent(reader, args);
            }catch (IOException ignored){
                ignored.printStackTrace();
            }
            HelperFunctions.sendMail(mailTo, subject, mailBody);
        }
    }

    @Override
    public void weatherChanged(Rencontre rencontre, Meteo meteo, Meteo newMeteo) {
        String subject = "Darfoot changement de la meteo";

        for(RencontreUser player : rencontre.getPlayers()){
            String mailTo = player.getPlayer().getEmail();
            String mailBody = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("Ressources/templates/WeatherChanged.xhtml"))))) {
                Map<String, String> args = new HashMap<>();
                args.put("__firstName__", player.getPlayer().getFirstName());
                args.put("__MatchDate__", rencontre.getDateDebut().toString());
                args.put("__satdeName__", rencontre.getStade().getNom());
                args.put("__link__", Consts.WEB_ROOT + "/rencontre.html?id=" + rencontre.getId());
                mailBody = getTemplateContent(reader, args);
            }catch (IOException ignored){
                ignored.printStackTrace();
            }
            HelperFunctions.sendMail(mailTo, subject, mailBody);
        }
    }
    public void gameCanceled(Rencontre rencontre){
        String subject = "Darfoot : Match annulé";
        for(RencontreUser player : rencontre.getPlayers()){
            String mailTo = player.getPlayer().getEmail();
            String mailBody = "";
            if(player.getPlayer().getId() != rencontre.getOrganizer().getId()){
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("/Ressources/templates/GameCanceled.xhtml"))))) {
                    Map<String, String> args = new HashMap<>();
                    args.put("__firstName__", player.getPlayer().getFirstName());
                    args.put("__MatchDate__", rencontre.getDateDebut().toString());
                    args.put("__satdeName__", rencontre.getStade().getNom());
                    mailBody = getTemplateContent(reader, args);
                }catch (IOException ignored){
                    ignored.printStackTrace();
                }
            }else{
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("Ressources/templates/GameCanceled.xhtml"))))) {
                    Map<String, String> args = new HashMap<>();
                    args.put("__firstName__", player.getPlayer().getFirstName());
                    args.put("__MatchDate__", rencontre.getDateDebut().toString());
                    args.put("__satdeName__", rencontre.getStade().getNom());
                    mailBody = getTemplateContent(reader, args);
                }catch (IOException ignored){
                    ignored.printStackTrace();
                }
            }
            System.out.println(mailBody);
            HelperFunctions.sendMail(mailTo,subject,mailBody);
        }
    }
}
