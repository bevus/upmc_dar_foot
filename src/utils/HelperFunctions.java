package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import init.DailyTask;
import models.Meteo;
import models.Rencontre;
import models.Stade;
import models.User;
import org.hibernate.SessionFactory;

import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static Date formatDate(Date d){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {
            d = format.parse(format.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
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
        Date date = formatDate(new Date());
        cal.setTime(date);
        for (JsonNode node : days){
            ObjectNode day = daysResponse.addObject();
            day.put("name", daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1]);
            day.put("date", cal.getTime().getTime());
            day.put("dayT", (int)Double.parseDouble(node.get("temp").get("day").asText()));
            day.put("nightT", (int)Double.parseDouble(node.get("temp").get("night").asText()));
            day.put("icon", "http://openweathermap.org/img/w/" + node.get("weather").get(0).get("icon").asText() + ".png");
            day.put("humidity", node.get("humidity").asText());
            day.put("description", node.get("weather").get(0).get("description").asText());
            day.put("code", Integer.parseInt(node.get("weather").get(0).get("id").asText())/100);
            cal.add(Calendar.DATE, 1);
        }
        return response;
    }

    public static ArrayList<Meteo> jsonToMeteo(ObjectNode node, Stade stade, Rencontre rencontre){
        ArrayList<Meteo> meteos = new ArrayList<>();
        for(JsonNode day : (ArrayNode)node.get("days")){
            Meteo meteo = new Meteo();
            meteo.setStade(stade);
            meteo.setDayName(day.get("name").asText());
            meteo.setDayT((int)Double.parseDouble(day.get("dayT").asText()));
            meteo.setNightT((int)Double.parseDouble(day.get("nightT").asText()));
            meteo.setHumidity(Integer.parseInt(day.get("humidity").asText()));
            meteo.setDescription(day.get("description").asText());
            meteo.setIcon(day.get("icon").asText());
            meteo.setDayDate(new Date(Long.parseLong(day.get("date").asText())));
            meteo.setCode(day.get("doce").asInt());
            meteo.setRencontre(rencontre);
            meteos.add(meteo);

        }
        return meteos;
    }

    public static Meteo filterMeteo(List<Meteo> meteoList, Date date){
        Collections.sort(meteoList);
        Calendar calendar = Calendar.getInstance();
        for(Meteo m : meteoList){
            if(m.getDayDate().compareTo(date) == 0){
                return  m;
            }
        }
        return meteoList.get(meteoList.size() - 1);
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
    public static String getPageTitle(String url){
        switch (url){
            case "/404.html":
                return "Page Introuvable";
            case "/liste.html":
                return "Rencontres";
            case "/organiserMatch.html":
                return "Organiser un match";
            case "/rencontre.html":
                return "Match";
            case "updateProfile.html":
                return "Profile";
            default:
                return "Foot";
        }
    }
    public static String header(String title, User user){
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder html = new StringBuilder();
                html.append(
                        "<!DOCTYPE html>\n").append(
                        "<html lang=\"en\">\n").append(
                        "<head>\n").append(
                        "    <meta charset=\"UTF-8\">\n").append(
                        "    <link rel=\"stylesheet\" href=\"Ressources/js/bower_components/bootstrap/dist/css/bootstrap.min.css\">\n").append(
                        "    <link rel=\"stylesheet\" href=\"Ressources/js/bower_components/eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.min.css\">\n").append(
                        "    <link rel=\"stylesheet\" href=\"Ressources/css/style.css\">\n").append(
                        "    <script src=\"Ressources/js/bower_components/jquery/dist/jquery.min.js\"></script>\n").append(
                        "    <script src=\"Ressources/js/bower_components/bootstrap/dist/js/bootstrap.min.js\"></script>").append(
                        "    <script src=\"Ressources/js/functions.js\"></script>").append("<title>").append(title).append("</title>\n").append(
                "\n" ).append(
                "</head>\n" ).append(
                "<body>");
            if(user == null){
                html.append("<nav class=\"navbar navbar-default navbar-static-top\">\n" ).append(
                        "    <div class=\"container\">\n" ).append(
                        "        <a href=\"index.html\" class=\"navbar-brand\">Foot</a>\n" ).append(
                        "        <ul class=\"nav navbar-nav\">\n" ).append(
                        "            <li><a href=\"\" data-toggle=\"modal\" data-target=\"#loginModal\">organiser un match</a></li>\n" ).append(
                        "            <li><a href=\"/liste.html\">trouver un match</a></li>\n" ).append(
                        "        </ul>\n" ).append(
                        "        <a href=\"\" data-toggle=\"modal\" data-target=\"#loginModal\" class=\"btn btn-primary navbar-btn pull-right\">Se Connecter</a>\n" ).append(
                        "    </div>\n" ).append(
                        "</nav>");
                html.append( "<div id=\"loginModal\" class=\"modal fade\" tabindex=\"-1\" role=\"dialog\">\n" ).append(
                        "    <div class=\"modal-dialog\" role=\"document\">\n" ).append(
                        "        <div class=\"modal-content\">\n" ).append(
                        "            <div class=\"modal-header\">\n" ).append(
                        "                <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>\n" ).append(
                        "                <h4 class=\"modal-title\">Se Connecter</h4>\n" ).append(
                        "            </div>\n" ).append(
                        "            <form class=\"form-horizontal\" id=\"login_form\">\n" ).append(
                        "                <div class=\"modal-body\">\n" ).append(
                        "                    <div class=\"form-group\">\n" ).append(
                        "                        <label class=\"col-md-4 control-label\" for=\"login_email\">adresse mail</label>\n" ).append(
                        "                        <div class=\"col-md-5\">\n" ).append(
                        "                            <input id=\"login_email\" name=\"login_email\" type=\"email\" placeholder=\"placeholder\" class=\"form-control input-md\" required=\"\">\n" ).append(
                        "                            <span class=\"help-block alert-danger\"></span>\n" ).append(
                        "                        </div>\n" ).append(
                        "                    </div>\n" ).append(
                        "\n" ).append(
                        "                    <div class=\"form-group\">\n" ).append(
                        "                        <label class=\"col-md-4 control-label\" for=\"login_password\">mot de passe</label>\n" ).append(
                        "                        <div class=\"col-md-5\">\n" ).append(
                        "                            <input id=\"login_password\" name=\"login_password\" type=\"password\" placeholder=\"placeholder\" class=\"form-control input-md\" required=\"\">\n" ).append(
                        "                            <span id=\"login_error\" class=\"help-block alert-danger\"></span>\n" ).append(
                        "                        </div>\n" ).append(
                        "                    </div>\n" ).append(
                        "                </div>\n" ).append(
                        "                <div class=\"modal-footer\">\n" ).append(
                        "                    <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">fermer</button>\n" ).append(
                        "                    <button id=\"login_submit\" name=\"submitSingup\" class=\"btn btn-primary\">Connexion</button>\n" ).append(
                        "                </div>\n" ).append(
                        "            </form>\n" ).append(
                        "        </div>\n" ).append(
                        "    </div>\n" ).append(
                        "</div>");
                html.append( "<script> user = undefined;").append(
                        "       $(function(){" ).append(
                        "           $('#login_form').submit(function(e){\n" ).append(
                        "                e.preventDefault();\n" ).append(
                        "                $('#login_submit').addClass('disabled');\n" ).append(
                        "                $.post('/login', $(this).serialize(), function (response) {\n" ).append(
                        "                    if(response.ok){\n" ).append(
                                                 "var element = $(e.target);\n" ).append(
                                "                if(element.attr(\"href\")){\n" ).append(
                                "                    window.location.replace(element.attr(\"href\"));\n" ).append(
                                "                }else{\n" ).append(
                                "                    window.location.reload();\n" ).append(
                                "                }" ).append(
                        "                    }else{\n" ).append(
                        "                        $('#login_submit').removeClass('disabled');\n" ).append(
                        "                        $('#login_error').text(\"email ou mot de passe incorect\");\n" ).append(
                        "                    }\n" ).append(
                        "                } , 'json');\n" ).append(
                        "            });" ).append(
                        "       });").append(
                        "</script>");
            }else{
                // connected
                try {
                    html.append( "<nav class=\"navbar navbar-default navbar-static-top\">\n" ).append(
                            "    <div class=\"container\">\n" ).append(
                            "        <a href=\"/index.html\" class=\"navbar-brand\">Foot</a>\n" ).append(
                            "        <ul class=\"nav navbar-nav\">\n" ).append(
                            "            <li><a href=\"/organiserMatch.html\">organiser un match</a></li>\n" ).append(
                            "            <li><a href=\"/liste.html\">trouver un match</a></li>\n" ).append(
                            "        </ul>\n" ).append(
                            "        <ul class=\" list-unstyled navbar-right\">\n" ).append(
                            "            <li>\n" ).append(
                            "                <img style=\"position: relative;top: 5px;\" src=\"/Ressources/images/" ).append( user.getImg() ).append( "\" width=\"40\" height=\"40\" class=\"dropdown-toggle img-thumbnail img-circle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">\n" ).append(
                            "                <ul class=\"dropdown-menu\">\n" ).append(
                            "                    <li><a href=\"/updateProfile.html\"><span class=\"glyphicon glyphicon-cog\"></span> Mon profil</a></li>\n" ).append(
                            "                    <li role=\"separator\" class=\"divider\"></li>\n" ).append(
                            "                    <li><a href=\"\" id=\"logout\"><span class=\"glyphicon glyphicon-log-out\"></span> D&eacute;connexion</a></li>\n" ).append(
                            "                </ul>\n" ).append(
                            "            </li>\n" ).append(
                            "        </ul>\n" ).append(
                            "    </div>\n" ).append(
                            "</nav>" ).append(
                            "<script>\n").append("user = ").append(mapper.writeValueAsString(user)).append(";\n"
                            ).append("$(function () {\n" ).append(
                            "        $(\"#logout\").click(function (e) {\n" ).append(
                            "            e.preventDefault();\n" ).append(
                            "            $.post('/logout', {}, function (data) {\n" ).append(
                            "                if(data.ok){\n" ).append(
                            "                    window.location.href = \"/\";\n" ).append(
                            "                }\n" ).append(
                            "            }, 'json');\n" ).append(
                            "        });\n" ).append(
                            "    });").append(
                            "</script>");
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        return html.toString();
    }
    public static String footer(){
        String html = "";
        html += "</body></html>";
        return html;
    }
    public static String getContent(BufferedReader reader) throws IOException {
        String content = "";
        String line;
        while ((line = reader.readLine()) != null){
            content += line + "\n";
        }
        return content;
    }
    private void writeFile(Part part, String fileName ) throws IOException {
        Files.copy(part.getInputStream(), Paths.get(fileName));
    }

    public static void StartDailyTask(SessionFactory sessionFactory){
        Timer timer= new Timer();
        timer.schedule(new DailyTask(sessionFactory),1000,10000*6*60*24); // 24 heures
    }
}