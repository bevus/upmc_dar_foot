package forms;
import models.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hacene on 11/02/2016.
 */
public abstract class Form {
    public String result;
    public Map<String,String> error = new HashMap<>();
    protected SessionFactory factory = null;
    public static int NAME_MIN_SIZE = 2;
    public static int NAME_MAX_SIZE = 60;
    public static int COMMENT_MAX_SIZE = 255;

    public Form(SessionFactory factory) {
        this.factory = factory;
    }
    //      recupere la valuer des parametres depuis la requette
    public static String getField(String name, HttpServletRequest request){
        String champ = request.getParameter(name);
        if(champ == null || champ.trim().isEmpty()){
            return null;
        }else{
            return champ;
        }
    }

    public static void checkPassword(String password, String cPassword) throws Exception {
        if(password == null || cPassword == null){
            throw new Exception("mot de passe vide");
        }else if(!password.equals(cPassword)){
            throw new Exception("les deux mots de passes ne correspondent pas");
        }
    }

    public static void checkName(String name) throws Exception{
        if(name == null){
            throw new Exception("champs vide");
        }else{
            if(name.length() < NAME_MIN_SIZE) {
                throw new Exception("au moins " + NAME_MIN_SIZE + " caractèrs");
            }
            if(name.length() > NAME_MAX_SIZE){
                throw new Exception("au plus " + NAME_MAX_SIZE + " caractèrs");
            }
        }
    }
    // validation d'une adresse mail
    public static void checkMail(String mail) throws Exception {
        if (mail == null) {
            throw new Exception("mail vide");
        } else if (!mail.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
            throw new Exception("format adresse mail incorrect");
        }
    }
    public static void isNewMail(String mail, SessionFactory factory) throws Exception {
        Session session = factory.openSession();
        if(session.createQuery("from User where email = :mail").setParameter("mail", mail).list().size() > 0){
            session.close();
            throw new Exception("adresse mail déja utilisée");
        }
        session.close();
    }
    public static User checkLogin(String email, String password, Session session){
        if(email == null || password == null){
            return null;
        }else{
            User  user = (User)session.createQuery("from User where email = :email and  password = :password").
                    setParameter("email", email).setParameter("password", password).uniqueResult();
            session.close();
            return user;
        }
    }
}
