package init;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Hacene on 07/10/2016.
 */
public class Init implements ServletContextListener {
    public static final String ATT_SESSION_FACTORY = "hibernateSessionFactory";
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        servletContext.setAttribute(ATT_SESSION_FACTORY, sessionFactory);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
