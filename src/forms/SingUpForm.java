package forms;

import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import utils.HelperFunctions;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by Hacene on 08/10/2016.
 */
public class SingUpForm extends Form {
    public static final String DEFAULT_USER_PIC = "1.png";
    public SingUpForm(SessionFactory factory) {
        super(factory);
    }

    public User validate(HttpServletRequest request){
        User user = new User();
        user.setFirstName(getField("firstName", request));
        user.setLastName(getField("lastName", request));
        user.setEmail(getField("email", request));
        user.setPassword(getField("password", request));
        user.setImg(DEFAULT_USER_PIC);
        user.setCreationDate(new Date());
        try{
            checkName(user.getFirstName());
        }catch (Exception e){
            error.put("firstName", e.getMessage());
        }
        try{
            checkName(user.getLastName());
        }catch (Exception e){
            error.put("lastName", e.getMessage());
        }
        try{
            checkMail(user.getEmail());
        }catch (Exception e){
            error.put("email", e.getMessage());
        }
        try {
            isNewMail(user.getEmail(), factory);
        }catch (Exception e){
            error.put("email", e.getMessage());
        }
        try{
            checkPassword(user.getPassword(), getField("confirmPassword", request));
        }catch (Exception e){
            error.put("password", e.getMessage());
        }

        if(error.isEmpty()){
            // encrypte password
            user.setPassword(HelperFunctions.getSHA1(user.getPassword()));
            // persist
            Session session = factory.openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            session.close();
            return user;
        }else{
            return null;
        }
    }
}
