package forms;

import models.User;
import org.hibernate.SessionFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by Hacene on 08/10/2016.
 */
public class SingUpForm extends Form {
    public SingUpForm(SessionFactory factory) {
        super(factory);
    }

    public User validate(HttpServletRequest request){
        User user = new User();
        user.setFirstName(getField("firstName", request));
        user.setLastName(getField("lastName", request));
        user.setEmail(getField("email", request));
        user.setPassword(getField("password", request));
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
            return user;
        }else{
            return null;
        }
    }
}
