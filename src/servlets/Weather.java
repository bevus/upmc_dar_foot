package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import utils.HelperFunctions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Hacene on 12/10/2016.
 */
public class Weather extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String lat = Form.getField("lat", request);
        String lon = Form.getField("lon", request);
        String nbDays = (Form.getField("nbDays", request) != null) ? Form.getField("nbDays", request) : "5";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();

        if(lat != null && lon != null){
            try {
                responseJson = HelperFunctions.getWeatherData(lat, lon, nbDays);
            } catch (Exception e) {
                responseJson.put("error", "impossible d'accéder au données météo");
            }
        }else{
            responseJson.put("error", "pas de latitude ou langitude");
        }
        response.setContentType("text/json");
        response.getWriter().print(responseJson.toString());
    }
}
