package servlets;

import init.DailyTask;
import init.Init;
import init.NotifyUsers;
import models.Meteo;
import models.Rencontre;
import models.Stade;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import utils.HelperFunctions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by khelifa on 08/11/2016.
 */
public class TestEnvoiMail extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        SessionFactory sessionFactory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        Session session = sessionFactory.openSession();

        List<Meteo> meteos = session.createQuery("from Meteo where dayDate >=:currDate order by dayDate")
                .setParameter("currDate", new Date())
                .list();
        if(meteos.isEmpty()) return;
        Meteo meteo = meteos.get(0);

        if(Integer.parseInt(request.getParameter("test"))==1) {
            meteo.setCode(meteo.getCode()-1);
        }else{
            meteo.setDayDate(DateUtils.addDays(meteo.getRencontre().getDateDebut(),1));
        }
        session.beginTransaction();
        session.update(meteo);
        session.getTransaction().commit();
        session.close();


        new Thread(new Runnable() {
            @Override
            public void run() {
                DailyTask task = new DailyTask(sessionFactory);
                task.addObserver(new NotifyUsers(getServletContext()));
                task.run();
            }
        }).start();


    }
}
