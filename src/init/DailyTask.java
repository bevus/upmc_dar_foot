package init;


import javafx.util.Pair;
import models.Meteo;
import models.Rencontre;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import utils.HelperFunctions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;


/**
 * Created by Zahir on 24/10/2016.
 */
public class DailyTask extends TimerTask implements Observable{
    private SessionFactory sessionFactory;
    private List<Observer> observers = new ArrayList<>();
    public DailyTask(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        try {
            System.out.println(new Date() + " Daily Task" );
            Session session = sessionFactory.openSession();

            List<Meteo> meteos = session.createQuery("from Meteo where dayDate >=:currDate")
                    .setParameter("currDate", new Date())
                    .list();
            session.beginTransaction();
            List<Pair<Meteo, Meteo>> oldNewMeteos = new ArrayList<>();

            for (Meteo oldMeteo: meteos){
                Meteo newMeteo = HelperFunctions.filterMeteo(HelperFunctions.getWeatherData(oldMeteo.getStade().getLatitude(), oldMeteo.getStade().getLongitude(), 16)
                        , oldMeteo.getRencontre().getDateDebut());
                oldNewMeteos.add(new Pair<>(oldMeteo.clone(), newMeteo));
                oldMeteo.setDayDate(newMeteo.getDayDate())
                        .setDayName(newMeteo.getDayName())
                        .setDayT(newMeteo.getDayT())
                        .setMin(newMeteo.getMin())
                        .setMax(newMeteo.getMax())
                        .setEve(newMeteo.getEve())
                        .setMorn(newMeteo.getMorn())
                        .setNightT(newMeteo.getNightT())
                        .setHumidity(newMeteo.getHumidity())
                        .setPressure(newMeteo.getPressure())
                        .setSpeed(newMeteo.getSpeed())
                        .setDescription(newMeteo.getDescription())
                        .setIcon(newMeteo.getIcon())
                        .setCode(newMeteo.getCode());

                session.update(oldMeteo);
            }
            session.getTransaction().commit();

            for(Pair<Meteo, Meteo> pairOldNew : oldNewMeteos){
                Meteo oldMeteo = pairOldNew.getKey();
                Meteo newMeteo = pairOldNew.getValue();
                Rencontre rencontre = oldMeteo.getRencontre();

                if(!DateUtils.isSameDay(oldMeteo.getDayDate(), rencontre.getDateDebut()) && DateUtils.isSameDay(newMeteo.getDayDate(), rencontre.getDateDebut())) {
                    System.out.println("Weather available for Match id " + rencontre.getId());
                    System.out.println("sending mails to players");
                    meteoAvailable(rencontre, oldMeteo , newMeteo);
                }

                if(oldMeteo.getCode()!= newMeteo.getCode()){
                    System.out.println("Weather changed for Match id " + rencontre.getId() + " Weather id " + oldMeteo.getId() + " old " + oldMeteo.getCode() + " new " + newMeteo.getCode());
                    System.out.println("sending mails to players");
                    meteoChanged(rencontre, oldMeteo , newMeteo);
                }
            }
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @Override
    public void meteoAvailable(Rencontre rencontre , Meteo meteo, Meteo newMeteo) {
        for (Observer o: observers){
            o.weatherAvailable(rencontre, meteo, newMeteo);
        }
    }

    @Override
    public void meteoChanged(Rencontre rencontre , Meteo meteo, Meteo newMeteo) {
        for (Observer o: observers){
            o.weatherChanged(rencontre, meteo, newMeteo);
        }
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }
}
