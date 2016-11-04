package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import init.Consts;
import init.Init;
import models.Comment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Hacene on 01/11/2016.
 */
public class StadeComments extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(Consts.CONTENT_TYPE_JSON);
        ObjectMapper mapper = new ObjectMapper();
        try {
            int stadeId = Integer.parseInt(Form.getField("stadeId", request));
            int first = Integer.parseInt(Form.getField("first", request));
            int count = Integer.parseInt(Form.getField("count", request));

            SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
            Session session = factory.openSession();
            List<Comment> comments = (List<Comment>) session.createQuery("from Comment where stade.id = :stadeId order by upvoters.size desc , dateComment desc").setParameter("stadeId", stadeId).
                    setFirstResult(first).setMaxResults(count).list();
            response.getWriter().print(mapper.writeValueAsString(comments));
            session.close();
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().print("'error': true");
        }

    }
}
