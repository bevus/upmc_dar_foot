package forms;

import models.Address;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Hacene on 26/10/2016.
 */
public class UpdateUserForm extends Form {
    public UpdateUserForm(SessionFactory factory) {
        super(factory);
    }

    public User validate(HttpServletRequest request, User user, ServletContext context) {
        Session session = factory.openSession();
        String imgFileName = null;
        Part part = null;
        try {
            part = request.getPart("img");
            /*if(part.getSubmittedFileName().length() > 0){
                try{
                    imgFileName = checkFile(part);
                }catch (Exception e){
                    error.put("img", e.getMessage());
                }
            }
            */
        }catch (Exception ignored){
            ignored.printStackTrace();
        }
        try{
            user.setFirstName(checkName(getField("firstName", request)));
        }catch (Exception e){
            error.put("firstName", e.getMessage());
        }
        try{
            user.setReceiveMail(checkReceiveMail(getField("receiveMail", request)));
        }catch (Exception e){
            error.put("receiveMail", e.getMessage());
        }
        try{
            user.setLastName(checkName(getField("lastName", request)));
        }catch (Exception e){
            error.put("lastName", e.getMessage());
        }
        if(getField("password", request) != null || getField("confirmPassword", request) != null){
            try{
                user.setPassword(checkPassword(getField("password", request), getField("confirmPassword", request)));
            }catch (Exception e){
                error.put("password", e.getMessage());
            }
        }
        if(getField("phoneNumber", request) != null){
            try{
                user.setPhoneNumber(checkPhoneNumber(getField("phoneNumber", request)));
            }catch (Exception e){
                error.put("phoneNumber", e.getMessage());
            }
        }

        Address addr = null;

        if(getField("number", request) != null || getField("street", request) != null ||
                getField("posteCode", request) != null || getField("city", request) != null) {
            addr = new Address();
            try{
                addr.setNumber(checkStreetNumber(getField("number", request)));
            }catch (Exception e){
                error.put("number", e.getMessage());
            }
            try {
                addr.setStreet(checkStreetName(getField("street", request)));
            } catch (Exception e) {
                error.put("street", e.getMessage());
            }
            try {
                addr.setPosteCode(checkZipCode(getField("posteCode", request)));
            } catch (Exception e) {
                error.put("posteCode", e.getMessage());
            }
            try {
                addr.setCity(checkStreetName(getField("city", request)));
            } catch (Exception e) {
                error.put("city", e.getMessage());
            }
        }

        if(error.isEmpty()){
            Address prevAddress = null;
            if(addr != null){
                prevAddress = user.getAddress();
                user.setAddress(addr);
            }
            if(imgFileName != null){
                imgFileName = user.getId() + "_" + imgFileName;
                String oldImg = user.getImg()+"";
                try {
                    part.write(context.getRealPath(IMG_PATH + imgFileName));
                    user.setImg(imgFileName);
                    if(!oldImg.equals(DEFAULT_USER_PIC)){
                        Files.delete(Paths.get(context.getRealPath(IMG_PATH + oldImg)));
                    }
                }catch (Exception ignored){
                }
            }
            session.beginTransaction();
            session.update(user);
            if(prevAddress != null){
                session.delete(prevAddress);
            }
            session.getTransaction().commit();
            session.close();
            return user;
        }
        session.close();
        return null;
    }
}
