package init;

import models.Meteo;
import models.Rencontre;

import java.util.Observable;


/**
 * Created by khelifa on 05/11/2016.
 */
public class NotifyUsers implements Observer {
    @Override
    public void weatherAvailable(Rencontre rencontre , Meteo meteo, Meteo newMeteo) {

    }

    @Override
    public void weatherChanged(Rencontre rencontre, Meteo meteo, Meteo newMeteo) {

    }
}
