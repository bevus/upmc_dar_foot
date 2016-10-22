package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import forms.ScheduleGameForm;
import models.Rencontre;
import models.RencontreUser;
import models.Stade;
import models.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by khelifa on 08/10/2016.
 */
public class OrganiserMatch extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        ScheduleGameForm gameForm = new ScheduleGameForm(factory);
        Rencontre rencontre = gameForm.validate(request, (User)request.getSession().getAttribute("user"));
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        if(rencontre != null){
            json.put("ok", true);
            json.put("id", rencontre.getId());
        }else{
            json.put("ok", false);
            ObjectNode errors = json.putObject("error");
            for (String key : gameForm.error.keySet()){
                errors.put(key, gameForm.error.get(key));
            }
        }
        response.setContentType("text/json");
        response.getWriter().print(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


    }
}
