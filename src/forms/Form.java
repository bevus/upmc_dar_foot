package forms;

import models.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Hacene on 11/02/2016.
 */
public abstract class Form {
    public static final int MAX_PLAYERS_PER_TEAM = 11;
    public static final int MIN_PLAYERS_PAR_TEAM = 1;
    public static final int GAME_INFO_MIN_LENGTH = 30;
    public static final int GAME_INFO_MAX_LENGTH = 255;
    public static final int GAME_DURATION = 1000 * 3600 * 2;
    public static final int NEXT_GAME_MIN_TIME = 1000 * 3600 * 2; // 2 heures min avant un match et sa planification
    public static final int NEXT_GAME_MIN_TIME_HOURS = NEXT_GAME_MIN_TIME / 3600 / 1000; // 2 heures min avant un match et sa planification
    public static final int MAX_UPLOAD_SIZE_M = 1024*1024*10;
    public static final int NAME_MIN_SIZE = 2;
    public static final int NAME_MAX_SIZE = 60;
    public static final int COMMENT_MAX_SIZE = 255;
    public static final String TEAM_A = "A";
    public static final String TEAM_B = "B";
    public static final String DEFAULT_USER_PIC = "1.png";
    public static final String IMG_PATH = "/Ressources/images/";
//    public static final String[] VALID_MIME_TYPE = {"png", "jpg", "jpeg", "gif", "bmp"};

    public String result;
    public Map<String,String> error = new HashMap<>();
    public SessionFactory factory = null;

    public Form(SessionFactory factory) {
        this.factory = factory;
    }
    //      recupere la valuer des parametres depuis la requette
    public static String getField(String name, HttpServletRequest request){
        String champ = StringEscapeUtils.escapeHtml4(request.getParameter(name));
        if(champ == null || champ.trim().isEmpty()){
            return null;
        }else{
            return champ;
        }
    }

    public static String checkPassword(String password, String cPassword) throws Exception {
        if(password == null || cPassword == null){
                    throw new Exception("mot de passe vide");
        }else if(!password.equals(cPassword)){
            throw new Exception("les deux mots de passes ne correspondent pas");
        }
        return password;
    }

    public static String checkName(String name) throws Exception{
        if(name == null){
            throw new Exception("champs vide");
        }else{
            if(name.length() < NAME_MIN_SIZE) {
                throw new Exception("au moins " + NAME_MIN_SIZE + " caractèrs");
            }
            if(name.length() > NAME_MAX_SIZE){
                throw new Exception("au plus " + NAME_MAX_SIZE + " caractèrs");
            }
        }
        return name;
    }
    // validation d'une adresse mail
    public static String checkMail(String mail) throws Exception {
        if (mail == null) {
            throw new Exception("mail vide");
        } else if (!mail.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
            throw new Exception("format adresse mail incorrect");
        }
        return mail;
    }
    public static void isNewMail(String mail, SessionFactory factory) throws Exception {
        Session session = factory.openSession();
        if(session.createQuery("from User where email = :mail").setParameter("mail", mail).list().size() > 0){
            session.close();
            throw new Exception("adresse mail déja utilisée");
        }
        session.close();
    }
    public static User checkLogin(String email, String password, Session session){
        if(email == null || password == null){
            return null;
        }else{
            User  user = (User)session.createQuery("from User where email = :email and  password = :password").
                    setParameter("email", email).setParameter("password", password).uniqueResult();
            session.close();
            return user;
        }
    }
    public static String checkStadeComment(String comment) throws Exception {
        if(comment == null){
            throw new Exception("commentaire vide");
        }else{
            if(comment.length() > COMMENT_MAX_SIZE){
                throw new Exception("commentaire trop long, maximum "+COMMENT_MAX_SIZE);
            }
        }
        return comment;
    }
    public static Rencontre checkIdRencontre(String idString, Session session) throws Exception {
        if(idString == null){
            throw new Exception("rencontre invalide");
        }else{
            int id;
            try{
                id = Integer.parseInt(idString);
            }catch (NumberFormatException e){
                throw new Exception("rencontre invalide");
            }

            Rencontre rencontre = session.get(Rencontre.class, id);
            if(rencontre == null){
                throw new Exception("rencontre invalide");
            }
            return rencontre;
        }
    }

    public static void checkIdStade(String id, Session session) throws Exception{
        if(id == null){
            throw new Exception("stade inconnu");
        }else{
            int intId;
            try{
                intId = Integer.parseInt(id);
                Stade stade = session.get(Stade.class, intId);
                if(stade == null){
                    throw new Exception("stade inconnu");
                }
            }catch (NumberFormatException e){
                throw new Exception("stade inconnu");
            }
        }
    }

    public static void canPlay(Rencontre rencontre, User user, String team) throws Exception{
        if(team == null){
            throw new Exception("équipe non spécifiée");
        }else if (!team.equals(TEAM_A) && !team.equals(TEAM_B)){
            throw new Exception("équipe invalide, choix possible : '" + TEAM_A + "', '" + TEAM_B + "'");
        }else if(rencontre == null){
            throw new Exception("rencontre invalide");
        }else if(user == null){
            throw new Exception("connectez vous pour pouvoir participer à une rencontre");
        }else if (rencontre.getOrganizer().getId() == user.getId()){
            throw new Exception("Vous êtes déja enregistré comme participant à cette rencontre");
        }else{
            int teamACount = 0;
            int teamBCount = 0;
            for(RencontreUser player : rencontre.getPlayers()){
                if(player.getPlayer().getId() == user.getId()){
                    throw new Exception("Vous êtes déja enregistré comme participant à cette rencontre");
                }
                if(player.getTeam().equals(TEAM_A)){
                    teamACount++;
                }else if(player.getTeam().equals(TEAM_B)){
                    teamBCount++;
                }
            }
            switch (team){
                case TEAM_A:
                    if(teamACount >= rencontre.getNbJoueurs()){
                        throw new Exception("équipe " + TEAM_A + "complète");
                    }
                    break;
                case TEAM_B:
                    if(teamBCount >= rencontre.getNbJoueurs()){
                        throw new Exception("équipe " + TEAM_B + "complète");
                    }
                    break;
            }
        }
    }
    //    stade // verifier l'existance du stade
    public static Stade checkStade(String stadeId, Session session) throws Exception{
        if(stadeId == null){
            throw new Exception("Stade inconnu");
        }else{
            int id;
            try {
                id = Integer.parseInt(stadeId);
            }catch (NumberFormatException e){
                throw new Exception("Stade inconnu");
            }
            Stade stade = session.get(Stade.class, id);
            if(stade == null){
                throw new Exception("Stade inconnu");
            }

            return stade;
        }
    }
    // nbJoueur // pas plus de 11 par equipe
    public static int checkNbPlayers(String n) throws Exception {
        if(n == null){
            throw new Exception("nombre de joueurs invalide");
        }else {
            int nb;
            try {
                nb = Integer.parseInt(n);
            }catch (NumberFormatException e){
                throw new Exception("nombre de joueurs invalide");
            }
            if(nb > MAX_PLAYERS_PER_TEAM || nb < MIN_PLAYERS_PAR_TEAM){
                throw new Exception("nombre de joueurs invalide : au plus " + MAX_PLAYERS_PER_TEAM + " joueurs , au moins " + MIN_PLAYERS_PAR_TEAM + " joueur par équipe");
            }
            return nb;
        }
    }
    // description // pas vide
    public static String checkGameInformation(String text) throws Exception{
        if(text == null){
            throw new Exception("Champ obligatoir");
        }else{
            if(text.length() < GAME_INFO_MIN_LENGTH){
                throw new Exception("text trop court : au moins " + GAME_INFO_MIN_LENGTH);
            }else if( text.length() > GAME_INFO_MAX_LENGTH){
                throw new Exception("text trop long : au plus " + GAME_INFO_MAX_LENGTH);
            }
            return text;
        }
    }
    // qu'il n y a pas rencontre le jour meme dans le meme stade et que l'oganisateur ne participe pas a un autre match ce jour la
    public static Date checkGameDate(String dateString, Stade stade, User player, Session session) throws Exception{
        if(player == null){
            throw new Exception("vous n'êtes pas connecté");
        }
        if(stade == null){
            throw new Exception("vous devez choisir un stade");
        }
        if(dateString == null){
            throw new Exception("vous devez indiquer une date");
        }else{
            long time;
            try{
                time = Long.parseLong(dateString);
            }catch (NumberFormatException e){
                throw new Exception("date non valide");
            }

            Date date = new Date(time);
            Date cDate = new Date(System.currentTimeMillis() + NEXT_GAME_MIN_TIME);
            if(date.compareTo(cDate) <= 0){
                throw new Exception("date non valide : au moins " + NEXT_GAME_MIN_TIME_HOURS + " heures avant le debut de la rencontre");
            }else{
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                String fDate = format.format(date);

                List<Rencontre> rencontres = session.createQuery("from Rencontre where stade.id = :id and date_format(dateDebut, '%d-%m-%Y') = :gameDay").
                        setParameter("id", stade.getId()).setParameter("gameDay", fDate).list();
                for(Rencontre r : rencontres){
                    if(Math.abs(date.getTime() - r.getDateDebut().getTime()) <= GAME_DURATION){
                        throw new Exception("une rencontre est déja organizée dans ce stade à la même heure");
                    }
                }
                rencontres = session.createQuery("from RencontreUser where date_format(rencontre.dateDebut, '%d-%m-%Y') = :gameDate and player.id = :id").
                        setParameter("id", player.getId()).setParameter("gameDate", fDate).list();

                if(!rencontres.isEmpty()){
                    throw new Exception("Vous participez déja à une rencontre ce jour la, choisissez un autre jour");
                }
            }
            return date;
        }
    }
    // check file extention
    // check file lenght
    public static String checkFile(Part part) throws Exception{
        if(part == null){
            throw new Exception("erreur lors de la transmission du fichier");
        }else{
            String mimeType = part.getContentType();
            if(!mimeType.split("/")[0].equals("image")){
                throw new Exception("le type du fichier n'est pas valide");
            }else{
                String fileExtention = mimeType.split("/")[1];
                if(part.getSize() > MAX_UPLOAD_SIZE_M){
                    System.out.println(part.getSize());
                    throw new Exception("taile du fichier trop grande au plus " + MAX_UPLOAD_SIZE_M / 1024 + " Mo");
                }
                return System.currentTimeMillis() + "." + fileExtention;
            }
        }
    }

    public static int checkStreetNumber(String streetNumber) throws Exception{
        // n° rue
        if(streetNumber == null ){
            throw new Exception("numéro de rue vide");
        }else{
            try{
                return Integer.parseInt(streetNumber);
            }catch (NumberFormatException e){
                throw new Exception("numéro de rue invalide");
            }
        }
    }

    public static String checkStreetName(String street) throws Exception{
        if(street == null){
            throw new Exception("nom de rue vide");
        }else{
            return street;
        }
    }

    public static String checkZipCode(String zipCode) throws Exception{
        if(zipCode == null){
            throw new Exception("code postal vides");
        }else{
            if(!zipCode.matches("^[0-9]{5}$")){
                throw new Exception("code postal invalide");
            }else{
                return zipCode;
            }
        }
    }

    public static String checkPhoneNumber(String phoneNumber) throws Exception{
        if(phoneNumber == null){
            throw new Exception("numéro de télephone vide");
        }else{
            return phoneNumber;
        }
    }
    public static void canUpvoteComment(User user, Comment comment) throws Exception{
        if(user == null){
            throw new Exception("pas connecté");
        }else if(comment == null){
            throw new Exception("commentaire invalide");
        }else{
            for(User u : comment.getUpvoters()){
                if(u.getId() == user.getId()){
                    throw new Exception("vous avez déja voter pour ce commentaire");
                }
            }
        }
    }

    public static void canUpvoteSatde(User user, Stade stade) throws Exception{
        if(user == null){
            throw new Exception("pas connecté");
        }else if(stade == null){
            throw new Exception("stade invalide");
        }else{
            for(User u : stade.getStadeUpvoters()){
                if(u.getId() == user.getId()){
                    throw new Exception("vous avez déja voter pour ce stade");
                }
            }
        }
    }
}
