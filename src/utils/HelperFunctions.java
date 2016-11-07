package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import init.DailyTask;
import init.NotifyUsers;
import models.Meteo;
import models.Rencontre;
import models.Stade;
import models.User;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.SessionFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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
    public static final String[] daysOfWeek = {"Dimanche", "Lundi","Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};

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

    public static List<Meteo> getWeatherData(double lat, double lon, int nbDays) throws Exception{
        final String API_KEY = "d651f5d56cfe0880876e540f4a805bdc";
        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" +
                lat + "&lon=" + lon + "&cnt=" + nbDays + "&mode=json&units=metric&lang=fr&appid="+API_KEY;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = getNode(url, mapper);
        JsonNode days = root.get("list");
        List<Meteo> response = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        Date date = formatDate(new Date());
        cal.setTime(date);
        for (JsonNode node : days){
            Meteo day = new Meteo();
            day.setDayName(daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1]);
            day.setDayDate(cal.getTime());
            day.setDayT((int)node.get("temp").get("day").asDouble());
            day.setMin((int)node.get("temp").get("min").asDouble());
            day.setMax((int)node.get("temp").get("max").asDouble());
            day.setNightT((int)node.get("temp").get("night").asDouble());
            day.setEve((int)node.get("temp").get("eve").asDouble());
            day.setMorn((int)node.get("temp").get("morn").asDouble());
            day.setIcon("http://openweathermap.org/img/w/" + node.get("weather").get(0).get("icon").asText() + ".png");
            day.setHumidity(node.get("humidity").asInt());
            day.setPressure(node.get("pressure").asInt());
            day.setSpeed(node.get("speed").asDouble());
            day.setDescription(node.get("weather").get(0).get("description").asText());
            day.setCode(node.get("weather").get(0).get("id").asInt()/100);
            response.add(day);
            cal.add(Calendar.DATE, 1);
        }
        return response;
    }

    public static Meteo filterMeteo(List<Meteo> meteoList, Date date){
        Collections.sort(meteoList);
        for(Meteo m : meteoList){
            if(DateUtils.isSameDay(m.getDayDate(), date)){
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
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
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
                        "    <script src=\"Ressources/js/script.js\"></script>").append("<title>").append(title).append("</title>\n").append(
                        "    <script src=\"Ressources/js/functions.js\"></script>").append("<title>").append(title).append("</title>\n").append(
                "\n" ).append(
                "</head>\n" ).append(
                "<body>");
            if(user == null){
                html.append("<nav class=\"navbar navbar-default navbar-fixed-top\">\n" ).append(
                        "    <div class=\"container\">\n" ).append(
                        "        <a href=\"index.html\" class=\"navbar-brand\">Foot</a>\n" ).append(
                        "        <ul class=\"nav navbar-nav\">\n" ).append(
                        "            <li><a href=\"\" data-href=\"/organiserMatch.html\" data-toggle=\"modal\" class=\"loginLink\" data-target=\"#loginModal\">organiser un match</a></li>\n" ).append(
                        "            <li><a href=\"/liste.html\">trouver un match</a></li>\n" ).append(
                        "        </ul>\n" ).append(
                        "        <a href=\"\" data-toggle=\"modal\" id=\"loginButton\" data-target=\"#loginModal\" class=\"btn loginLink btn-primary navbar-btn pull-right\">Se Connecter</a>\n" ).append(
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
                html.append("<script> user = undefined;").append("</script>");
            }else{
                // connected
                try {
                    html.append( "<nav class=\"navbar navbar-default navbar-fixed-top\">\n" ).append(
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
                            "                    <li><a href=\"/mesMatchs.html\"><span class=\"glyphicon glyphicon-list\"></span> Men matches</a></li>\n" ).append(
                            "                    <li role=\"separator\" class=\"divider\"></li>\n" ).append(
                            "                    <li><a href=\"\" id=\"logout\"><span class=\"glyphicon glyphicon-log-out\"></span> D&eacute;connexion</a></li>\n" ).append(
                            "                </ul>\n" ).append(
                            "            </li>\n" ).append(
                            "        </ul>\n" ).append(
                            "    </div>\n" ).append(
                            "</nav>" ).append(
                            "<script>").append("user = ").append(mapper.writeValueAsString(user)).append(";</script>");
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

    public static void StartDailyTask(SessionFactory sessionFactory, ServletContext servletContext){
        Timer timer= new Timer();
        DailyTask task = new DailyTask(sessionFactory);
        task.addObserver(new NotifyUsers(servletContext));
        timer.schedule(task,1000,10000*6*60*24); // 24 heures

    }

    public static void sendMail(String mailTo, String subject, String body){
        String host="smtp.live.com";
        final String user="darfoot@hotmail.com";
        final String password="Khelifa2016";

        //Get the session object
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", host);
        props.put("mail.smtp.host",host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");


        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(mailTo));
            message.setSubject(subject);
            message.setContent(body, "text/html");
            Transport.send(message);
            System.out.println("message sent successfully...");

        } catch (MessagingException e) {e.printStackTrace();}
    }

    public static void fillRencontreObjectNode(ObjectNode jsonR, Rencontre r){
        jsonR.put("organizerPic", r.getOrganizer().getImg());
        jsonR.put("organizerFirstName", r.getOrganizer().getFirstName());
        jsonR.put("organizerLastName", r.getOrganizer().getLastName());
        jsonR.put("dateTime", r.getDateDebut().getTime());
        jsonR.put("description", r.getDescription());
        jsonR.put("maxPlayersCount", r.getNbJoueurs() * 2);
        jsonR.put("playersCount", r.getPlayers().size());
        jsonR.put("city", r.getStade().getCommune());
        jsonR.put("id", r.getId());
    }
}