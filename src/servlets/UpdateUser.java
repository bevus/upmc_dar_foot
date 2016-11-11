package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.UpdateUserForm;
import init.Init;
import models.User;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Zahir on 15/10/2016.
 */
@WebServlet(name = "UpdateUser")
@MultipartConfig
public class UpdateUser extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/json; charset=utf-8");
        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        UpdateUserForm form = new UpdateUserForm(factory);
        User user = (User)request.getSession().getAttribute("user");
        ObjectMapper mapper = new ObjectMapper();

        if(user != null){
            user = form.validate(request, user, getServletContext());
            if(user == null){
                ObjectNode jsonRespons = mapper.createObjectNode();
                jsonRespons.put("error", true);
                System.out.println(form.error);
                for(String key : form.error.keySet()){
                    jsonRespons.put(key, form.error.get(key));
                }
                response.getWriter().print(jsonRespons.toString());
            }else{
                response.getWriter().print(mapper.writeValueAsString(user));
            }
        }else{
            ObjectNode jsonRespons = mapper.createObjectNode();
            jsonRespons.put("error", true);
            jsonRespons.put("msg", "connectez vous à votre profile pour continuer");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

