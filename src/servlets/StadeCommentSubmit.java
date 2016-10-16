package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.StadeCommentForm;
import init.Init;
import models.Comment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Hacene on 15/10/2016.
 */
public class StadeCommentSubmit extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/json");
        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        StadeCommentForm commentForm = new StadeCommentForm(factory);
        Comment comment = commentForm.validate(request);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResponse = mapper.createObjectNode();
        if(comment != null){
            response.getWriter().print(mapper.writeValueAsString(comment));
        }else{
            ObjectNode errors = jsonResponse.putObject("errors");
            for(String key : commentForm.error.keySet()){
                errors.put(key, commentForm.error.get(key));
            }
            response.getWriter().print(jsonResponse.toString());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
