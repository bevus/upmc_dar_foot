package init;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import utils.HelperFunctions;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Properties;

/**
 * Created by Hacene on 07/10/2016.
 */
public class Init implements ServletContextListener {
    public static final String ATT_SESSION_FACTORY = "hibernateSessionFactory";
    public static final String userMail="darfoot@hotmail.com";
    public static Session sessionMail;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        servletContext.setAttribute(ATT_SESSION_FACTORY, sessionFactory);

        String host="smtp.live.com";
        final String password="Khelifa2016";
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", host);
        props.put("mail.smtp.host",host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");


        sessionMail = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userMail, password);
                    }
                });

        DailyTask task = new DailyTask(sessionFactory);
        task.addObserver(new NotifyUsers(servletContext));
        new Thread(task).start();
        HelperFunctions.StartDailyTask(sessionFactory, sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
