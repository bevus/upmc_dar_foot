package init;

import models.Meteo;
import models.Rencontre;

/**
 * Created by khelifa on 05/11/2016.
 */
public interface Observer {
    public void weatherAvailable(Rencontre rencontre, Meteo meteo, Meteo newMeteo);
    public void weatherChanged(Rencontre rencontre, Meteo meteo, Meteo newMeteo);

}
