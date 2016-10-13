package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hacene on 08/10/2016.
 */
public class HelperFunctions {
    public static final String[] daysOfWeek = {"DIM", "LUN","MAR", "MER", "JEU", "VEN", "SAM"};
    public static String getSHA1(String s)  {
        if(s == null)
            return "";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(s.getBytes());
            byte[] mdbytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte mdbyte : mdbytes) {
                sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ObjectNode getWeatherData(String lat, String lon, String nbDays) throws Exception{
        final String API_KEY = "d651f5d56cfe0880876e540f4a805bdc";
        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" +
                lat + "&lon=" + lon + "&cnt=" + nbDays + "&mode=json&units=metric&lang=fr&appid="+API_KEY;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = getNode(url, mapper);
        JsonNode days = root.get("list");
        ObjectNode response = mapper.createObjectNode();
        ObjectNode city = response.putObject("city");
        city.put("name", root.get("city").get("name").asText());
        ArrayNode daysResponse = response.putArray("days");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        for (JsonNode node : days){
            ObjectNode day = daysResponse.addObject();
            day.put("name", daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1]);
            day.put("dayT", node.get("temp").get("day").asText());
            day.put("nightT", node.get("temp").get("night").asText());
            day.put("icon", "http://openweathermap.org/img/w/" + node.get("weather").get(0).get("icon").asText() + ".png");
            day.put("humidity", node.get("humidity").asText());
            day.put("description", node.get("weather").get(0).get("description").asText());
            cal.add(Calendar.DATE, 1);
        }
        return response;
    }

    /**
     *
     * @param query mot clé à rechercher par exemple début d'un code postal
     * @return  liste des communes de l'ile de france contenant ce mot clé
     * @throws Exception
     */
    public static ArrayNode getVilles(String query) throws Exception{
        // retourn un array de {codePostal, nom}
        String urlString = "https://data.iledefrance.fr/api/records/1.0/search/?dataset=les-communes-dile-de-france-au-01-janvier-20-geofla-ign&q=" +query+"&rows=100&pretty_print=true";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResponse = mapper.createObjectNode();
        JsonNode records = getNode(urlString, mapper).get("records");
        ArrayNode elements = jsonResponse.putArray("elements");

        for (JsonNode node : records){
            JsonNode fieds = node.get("fields");
            ObjectNode element = mapper.createObjectNode();
            element.put("codePostal", fieds.get("insee").asText());
            element.put("nom", fieds.get("nomcom").asText());
            elements.add(element);
        }
        return elements;
    }

    /**
     *
     * @param query url
     * @param mapper ObjectMapper
     * @return JsonNode de la reponse
     * @throws Exception
     */
    public static JsonNode getNode(String query, ObjectMapper mapper) throws Exception{
        URL url = new URL(query);
        URLConnection connection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return mapper.readTree(bufferedReader);
    }
}
