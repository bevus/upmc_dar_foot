package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
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

    public static String header(User user){
        String html =
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <link rel=\"stylesheet\" href=\"Ressources/js/bower_components/bootstrap/dist/css/bootstrap.min.css\">\n" +
                "    <link rel=\"stylesheet\" href=\"Ressources/css/style.css\">\n" +
                "    <script src=\"Ressources/js/bower_components/jquery/dist/jquery.min.js\"></script>\n" +
                "    <script src=\"Ressources/js/bower_components/bootstrap/dist/js/bootstrap.min.js\"></script>"+
                "    <title>SignUp</title>\n" +
                "\n" +
                "</head>\n" +
                "<body>";
            if(user == null){
                html+= "<nav class=\"navbar navbar-default navbar-static-top\">\n" +
                        "    <div class=\"container\">\n" +
                        "        <a href=\"index.html\" class=\"navbar-brand\">Foot</a>\n" +
                        "        <ul class=\"nav navbar-nav\">\n" +
                        "            <li><a href=\"\" data-toggle=\"modal\" data-target=\"#loginModal\">organiser un match</a></li>\n" +
                        "            <li><a href=\"/liste.html\">trouver un match</a></li>\n" +
                        "        </ul>\n" +
                        "        <a href=\"\" data-toggle=\"modal\" data-target=\"#loginModal\" class=\"btn btn-primary navbar-btn pull-right\">Se Connecter</a>\n" +
                        "    </div>\n" +
                        "</nav>";
                html+= "<div id=\"loginModal\" class=\"modal fade\" tabindex=\"-1\" role=\"dialog\">\n" +
                        "    <div class=\"modal-dialog\" role=\"document\">\n" +
                        "        <div class=\"modal-content\">\n" +
                        "            <div class=\"modal-header\">\n" +
                        "                <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>\n" +
                        "                <h4 class=\"modal-title\">Se Connecter</h4>\n" +
                        "            </div>\n" +
                        "            <form class=\"form-horizontal\" id=\"login_form\">\n" +
                        "                <div class=\"modal-body\">\n" +
                        "                    <div class=\"form-group\">\n" +
                        "                        <label class=\"col-md-4 control-label\" for=\"login_email\">adresse mail</label>\n" +
                        "                        <div class=\"col-md-5\">\n" +
                        "                            <input id=\"login_email\" name=\"login_email\" type=\"email\" placeholder=\"placeholder\" class=\"form-control input-md\" required=\"\">\n" +
                        "                            <span class=\"help-block alert-danger\"></span>\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "\n" +
                        "                    <div class=\"form-group\">\n" +
                        "                        <label class=\"col-md-4 control-label\" for=\"login_password\">mot de passe</label>\n" +
                        "                        <div class=\"col-md-5\">\n" +
                        "                            <input id=\"login_password\" name=\"login_password\" type=\"password\" placeholder=\"placeholder\" class=\"form-control input-md\" required=\"\">\n" +
                        "                            <span id=\"login_error\" class=\"help-block alert-danger\"></span>\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "                </div>\n" +
                        "                <div class=\"modal-footer\">\n" +
                        "                    <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">fermer</button>\n" +
                        "                    <button id=\"login_submit\" name=\"submitSingup\" class=\"btn btn-primary\">Connexion</button>\n" +
                        "                </div>\n" +
                        "            </form>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</div>";
                html += "<script>" +
                        "       $(function(){" +
                        "           $('#login_form').submit(function(e){\n" +
                        "                e.preventDefault();\n" +
                        "                $('#login_submit').addClass('disabled');\n" +
                        "                $.post('/login', $(this).serialize(), function (response) {\n" +
                        "                    if(response.ok){\n" +
                                                 "var element = $(e.target);\n" +
                                "                if(element.attr(\"href\")){\n" +
                                "                    window.location.replace(element.attr(\"href\"));\n" +
                                "                }else{\n" +
                                "                    window.location.reload();\n" +
                                "                }" +
                        "                    }else{\n" +
                        "                        $('#login_submit').removeClass('disabled');\n" +
                        "                        $('#login_error').text(\"email ou mote de passe incorect\");\n" +
                        "                    }\n" +
                        "                } , 'json');\n" +
                        "            });" +
                        "       });"+
                        "</script>";
            }else{
                html += "<nav class=\"navbar navbar-default navbar-static-top\">\n" +
                        "    <div class=\"container\">\n" +
                        "        <a href=\"/index.html\" class=\"navbar-brand\">Foot</a>\n" +
                        "        <ul class=\"nav navbar-nav\">\n" +
                        "            <li><a href=\"/organiserMatch.html\">organiser un match</a></li>\n" +
                        "            <li><a href=\"/liste.html\">trouver un match</a></li>\n" +
                        "        </ul>\n" +
                        "        <ul class=\" list-unstyled navbar-right\">\n" +
                        "            <li>\n" +
                        "                <img style=\"position: relative;top: 5px;\" src=\"/Ressources/images/" + user.getImg() + "\" width=\"40\" height=\"40\" class=\"dropdown-toggle img-thumbnail img-circle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">\n" +
                        "                <ul class=\"dropdown-menu\">\n" +
                        "                    <li><a href=\"#\">éditer mon profil</a></li>\n" +
                        "                    <li role=\"separator\" class=\"divider\"></li>\n" +
                        "                    <li><a href=\"\" id=\"logout\">se deconecter</a></li>\n" +
                        "                </ul>\n" +
                        "            </li>\n" +
                        "        </ul>\n" +
                        "    </div>\n" +
                        "</nav>" +
                        "<script>\n"
                        +"$(function () {\n" +
                        "        $(\"#logout\").click(function (e) {\n" +
                        "            e.preventDefault();\n" +
                        "            $.post('/logout', {}, function (data) {\n" +
                        "                if(data.ok){\n" +
                        "                    window.location.href = \"/\";\n" +
                        "                }\n" +
                        "            }, 'json');\n" +
                        "        });\n" +
                        "    });"+
                        "</script>";
            }
        return html;
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
}