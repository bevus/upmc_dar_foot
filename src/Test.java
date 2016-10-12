import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import org.hibernate.Session;
import utils.HelperFunctions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Hacene on 08/10/2016.
 */
public class Test {
    public static final String API_KEY = "d651f5d56cfe0880876e540f4a805bdc";
    public static final int NB_DAYS = 16;

    public static void main(String[] args) throws Exception {
        String lon = "2.287592000000018";
        String lat = "48.862725";
        System.out.println(getWeatherData(lat, lon, NB_DAYS, API_KEY));
    }

    public static String getWeatherData(String lat, String lon, int nbDays,String apiKey) throws Exception{
        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" +
                lat + "&lon=" + lon + "&cnt=" + nbDays + "&mode=json&units=metric&lang=fr&appid="+apiKey;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = getNode(url, mapper);
        JsonNode days = root.get("list");
        ObjectNode response = mapper.createObjectNode();
        ArrayNode daysResponse = response.putArray("days");
        for (JsonNode node : days){
            ObjectNode day = daysResponse.addObject();
            day.put("dayT", node.get("temp").get("day").asText());
            day.put("nightT", node.get("temp").get("night").asText());
            day.put("icon", "http://openweathermap.org/img/w/" + node.get("weather").get(0).get("icon").asText() + ".png");
            day.put("humidity", node.get("humidity").asText());
            day.put("description", node.get("weather").get(0).get("description").asText());
        }
        return response.toString();
    }

    public static ArrayNode getVilles(String query) throws Exception{
        // retourn un array de {codePostal, nom}
        String urlString = "https://data.iledefrance.fr/api/records/1.0/search/?dataset=les-communes-dile-de-france-au-01-janvier-20" + NB_DAYS + "-geofla-ign&q=" +query+"&rows=100&pretty_print=true";
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
    public static JsonNode getNode(String query, ObjectMapper mapper) throws Exception{
        URL url = new URL(query);
        URLConnection connection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return mapper.readTree(bufferedReader);
    }
    public static void populate(Session session, Date date){
        // addresses
        Address addressHacene = new Address();
        addressHacene.setCity("Villetaneuse");
        addressHacene.setNumber(21);
        addressHacene.setStreet("Marguerite Yourcenar");
        addressHacene.setPosteCode("93430");

        Address addressZahir = new Address();
        addressZahir.setCity("Paris");
        addressZahir.setNumber(75);
        addressZahir.setStreet("Charle Michel");
        addressZahir.setPosteCode("75015");

        Address addressKhelifa = new Address();
        addressKhelifa.setCity("Epinay");
        addressKhelifa.setNumber(75);
        addressKhelifa.setStreet("Paris");
        addressKhelifa.setPosteCode("93800");

        // players
        User hacene = new User();
        hacene.setAddress(addressHacene);
        hacene.setFirstName("Hacene");
        hacene.setLastName("Kedjar");
        hacene.setPassword(HelperFunctions.getSHA1("password"));
        hacene.setEmail("hacene.kedjar@gmail.com");
        hacene.setImg("1466032685.png");
        hacene.setCreationDate(new Date());

        User zahir = new User();
        zahir.setAddress(addressZahir);
        zahir.setPassword(HelperFunctions.getSHA1("password"));
        zahir.setFirstName("Zahir");
        zahir.setLastName("Chelbi");
        zahir.setEmail("zahir.chelbi@gmail.com");
        zahir.setImg("1466032719.png");
        zahir.setCreationDate(new Date());

        User khelifa = new User();
        khelifa.setAddress(addressKhelifa);
        khelifa.setPassword(HelperFunctions.getSHA1("password"));
        khelifa.setFirstName("Khelifa");
        khelifa.setLastName("Berrfas");
        khelifa.setEmail("khelifa.berffas.gmail.com");
        khelifa.setImg("1466356520.png");
        khelifa.setCreationDate(new Date());
        //stade
        Stade stade = new Stade();
        stade.setCodePostal(78057);
        stade.setCommune("Bennecourt");
        stade.setLatitude(49.03841000092038);
        stade.setLongitude(1.57235);
        stade.setNom("STADE AURÉLIEN BAZIN");
        stade.setNote(0);
        //comments
        Comment comment1 = new Comment();
        comment1.setStade(stade);
        comment1.setAuthor(hacene);
        comment1.setDateComment(new Date());
        comment1.setTextComment("Never hoist a bucaneer.All freebooters desire coal-black, stormy yardarms.");

        Comment comment2 = new Comment();
        comment2.setStade(stade);
        comment2.setAuthor(zahir);
        comment2.setDateComment(new Date());
        comment2.setTextComment("Never hoist a bucaneer.All freebooters desire coal-black, stormy yardarms.");

        Comment comment3 = new Comment();
        comment3.setStade(stade);
        comment3.setAuthor(khelifa);
        comment3.setDateComment(new Date());
        comment3.setTextComment("Never hoist a bucaneer.All freebooters desire coal-black, stormy yardarms.");
        // match
        Rencontre rencontre = new Rencontre();
        rencontre.setDescription("Arrr! Pieces o' hunger are forever old.Yuck, undead dubloon. you won't pull the fortress.Arg, heavy-hearted reef. you won't hoist the pacific ocean.Yuck! Pieces o' treasure are forever misty.Ho-ho-ho! strength of madness.");
        rencontre.setDateDebut(date);
        rencontre.setNbJoueurs(10);
        rencontre.setOrganizer(khelifa);

        rencontre.setStade(stade);
        rencontre.setPlayers(new ArrayList<>());
        rencontre.getPlayers().add(hacene);
        rencontre.getPlayers().add(zahir);
        rencontre.getPlayers().add(khelifa);

        session.beginTransaction();
        session.save(comment1);
        session.save(comment2);
        session.save(comment3);
        session.save(rencontre);
        session.getTransaction().commit();
        session.close();
    }
}
