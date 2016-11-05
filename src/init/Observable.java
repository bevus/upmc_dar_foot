package init;

import models.Meteo;
import models.Rencontre;


/**
 * Created by khelifa on 05/11/2016.
 */
public interface Observable {
    public void meteoAvailable(Rencontre rencontre, Meteo meteo, Meteo newMeteo);
    public void meteoChanged(Rencontre rencontre,Meteo meteo, Meteo newMeteo);

    public void addObserver(Observer o);
}
