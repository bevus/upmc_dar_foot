package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import init.Consts;
import init.Init;
import models.Comment;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Hacene on 30/10/2016.
 */
public class UpvoteComment extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(Consts.CONTENT_TYPE_JSON);
        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        User user = (User)request.getSession().getAttribute(Consts.USER);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResponse = mapper.createObjectNode();
        if(user != null){
            Session session = factory.openSession();
            String tmp = Form.getField("commentId", request);
            if(tmp != null){
                try{
                    int commentId = Integer.parseInt(tmp);
                    Comment comment = session.get(Comment.class, commentId);
                    if(comment != null){
                        try{
                            Form.canUpvoteComment(user, comment);
                            comment.getUpvoters().add(user);
                            session.beginTransaction();
                            session.update(comment);
                            session.getTransaction().commit();
                            jsonResponse.put("ok", true);
                            response.getWriter().print(jsonResponse.toString());
                            return;
                        }catch (Exception ignored){
                        }
                    }
                }catch (NumberFormatException ignored){
                }
            }
            session.close();
        }
        jsonResponse.put("error", true);
        response.getWriter().print(jsonResponse.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
