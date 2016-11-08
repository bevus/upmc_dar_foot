package servlets;

import init.Consts;
import models.User;
import utils.HelperFunctions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by Hacene on 22/10/2016.
 */
public class WrapHtml extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=utf-8");
        String fileName = request.getRequestURI();
        if(fileName.equals("/"))
            fileName = "/index.html";
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(request.getServletContext().getRealPath(fileName))))) {
            response.getWriter().println(HelperFunctions.header(HelperFunctions.getPageTitle(fileName), (User)request.getSession().getAttribute("user")));
            response.getWriter().print(HelperFunctions.getContent(reader));
            response.getWriter().println(HelperFunctions.footer());
        }catch (FileNotFoundException e){
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(request.getServletContext().getRealPath("/404.html"))))){
                response.getWriter().println(HelperFunctions.header(HelperFunctions.getPageTitle(fileName), (User)request.getSession().getAttribute("user")));
                response.getWriter().print(HelperFunctions.getContent(reader));
                response.getWriter().println(HelperFunctions.footer());
            }catch (Exception ignored){
            }
        }
    }
}
