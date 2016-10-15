package forms;

import models.Comment;
import models.Stade;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by Hacene on 15/10/2016.
 */
public class StadeCommentForm extends Form{

    public StadeCommentForm(SessionFactory factory) {
        super(factory);
    }
    public Comment validate(HttpServletRequest request){
        Session session = factory.openSession();
        Comment comment = new Comment();
        User user = (User)request.getSession().getAttribute("user");
        comment.setAuthor(user);
        comment.setDateComment(new Date());
        comment.setTextComment(getField("textComment", request));
        if(user == null){
            error.put("error", "vous devez vous connecter pour pouvoir poster un commentaire");
        }
        try{
            checkStadeComment(comment.getTextComment());
        }catch (Exception e){
            error.put("textComment", e.getMessage());
        }
        String idStade = getField("idStade", request);
        try{
            checkIdStade(idStade, session);
        }catch (Exception e){
            error.put("idSatde", e.getMessage());
        }
        if(error.isEmpty()){
            comment.setStade(session.get(Stade.class, Integer.parseInt(idStade)));
            session.close();
            return comment;
        }else{
            return null;
        }
    }
}
