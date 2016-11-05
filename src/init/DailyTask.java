package init;

import forms.Form;
import models.Meteo;
import models.Rencontre;
import models.Stade;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import utils.HelperFunctions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;


/**
 * Created by Zahir on 24/10/2016.
 */
public class DailyTask extends TimerTask implements init.Observable{
    private SessionFactory sessionFactory;
    private List<Observer> observers = new ArrayList<>();
    public DailyTask(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {

        try {
            System.out.println(new Date()+ " Daily Task" );

            Session session = sessionFactory.openSession();

            Date date = HelperFunctions.formatDate(new Date());
            List<Meteo> meteos = session.createQuery("from Meteo where dayDate >=:currDate")
                    .setParameter("currDate",HelperFunctions.formatDate(date))
                    .list();



//            System.out.println(meteos +"\n"+
//                    " "+meteos.size());

            session.beginTransaction();

            for (Meteo m: meteos){
                Meteo newMeteo=HelperFunctions.filterMeteo(HelperFunctions.jsonToMeteo(HelperFunctions.getWeatherData(m.getStade().getLatitude()+"",
                        m.getStade().getLongitude()+"", "16"), m.getStade(), m.getRencontre()),HelperFunctions.formatDate( m.getRencontre().getDateDebut()));


                //System.out.println(m.getDayT() + " "+ newMeteo.getDayT());

                m.setDayDate(newMeteo.getDayDate());
                m.setDayName(newMeteo.getDayName());
                m.setDayT(newMeteo.getDayT());
                m.setNightT(newMeteo.getNightT());
                m.setHumidity(newMeteo.getHumidity());
                m.setDescription(newMeteo.getDescription());
                m.setIcon(newMeteo.getIcon());
                m.setCode(newMeteo.getCode());

                if(HelperFunctions.formatDate(newMeteo.getDayDate()).compareTo(HelperFunctions.formatDate(m.getRencontre().getDateDebut()))==0){
                    meteoAvailable(m.getRencontre(), m , newMeteo);
                }

                if(m.getCode()!= newMeteo.getCode()){
                    meteoChanged(m.getRencontre(), m , newMeteo);
                }
                session.update(m);
            }

            session.getTransaction().commit();
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
