package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import models.Meteo;
import utils.HelperFunctions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Hacene on 12/10/2016.
 */
public class Weather extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String lat = Form.getField("lat", request);
        String lon = Form.getField("lon", request);
        String nbDays = Form.getField("nbDays", request);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();
        List<Meteo> meteos;
        if(lat != null && lon != null){
            try {
                meteos = HelperFunctions.getWeatherData(Double.parseDouble(lat), Double.parseDouble(lon), Integer.parseInt(nbDays));
                response.getWriter().print(mapper.writeValueAsString(meteos));
                return;
            } catch (Exception e) {
                responseJson.put("error", "impossible d'accéder au données météo");
            }
        }else{
            responseJson.put("error", "aucune latitude ou langitude");
        }
        response.setContentType("text/json; charset=utf-8");
        response.getWriter().print(responseJson.toString());
    }
}