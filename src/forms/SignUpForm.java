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
public class SignUpForm extends Form {
    public SignUpForm(SessionFactory factory) {
        super(factory);
    }

    public User validate(HttpServletRequest request){
        User user = new User();
        user.setImg(DEFAULT_USER_PIC);
        user.setCreationDate(new Date());
        try{
            user.setFirstName(checkName(getField("firstName", request)));
        }catch (Exception e){
            error.put("firstName", e.getMessage());
        }
        try{
            user.setLastName(checkName(getField("lastName", request)));
        }catch (Exception e){
            error.put("lastName", e.getMessage());
        }
        try{
            user.setEmail(checkMail(getField("email", request)));
        }catch (Exception e){
            error.put("email", e.getMessage());
        }
        try {
            isNewMail(user.getEmail(), factory);
        }catch (Exception e){
            error.put("email", e.getMessage());
        }
        try{
            user.setPassword(checkPassword(getField("password", request), getField("confirmPassword", request)));
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
