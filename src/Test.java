import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by Hacene on 08/10/2016.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
    }
}
