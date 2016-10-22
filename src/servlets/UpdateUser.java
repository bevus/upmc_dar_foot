package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.Form;
import init.Init;
import models.Address;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import utils.HelperFunctions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.List;

/**
 * Created by Zahir on 15/10/2016.
 */
@WebServlet(name = "UpdateUser")
public class UpdateUser extends HttpServlet {

    public static final String CHEMIN = "chemin";
    public static final int TAILLE_TAMPON = 10240;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String chemin =this.getServletConfig().getInitParameter(CHEMIN);

        User user =(User) request.getSession().getAttribute("user");
        int idUser = user.getId();

        String nomUser = Form.getField("firstName",request);//    request.getParameter("firstName");
        String prenomUser = Form.getField("lastName",request);//request.getParameter("lastName");
        String passUser = Form.getField("password",request);//request.getParameter("password");
        String telUser = Form.getField("firstName",request);//request.getParameter("phoneNumber");
        String rue = Form.getField("numberStreet",request) ;
        String code = Form.getField("CodePostal",request);
        String nomRue = Form.getField("nameStreet",request);
        String ville = Form.getField("city",request);

        Address address = new Address();
        if(rue !=null  && code !=null && nomRue !=null && ville != null) {
            int numberRue = Integer.valueOf(rue);
            address.setNumber(numberRue);
            address.setStreet(nomRue);
            address.setPosteCode(code);
            address.setCity(ville);
        }

        Part part = request.getPart("pictureUser");
        String nomFic = getNomFichier(part);
        if(nomFic != null && !nomFic.isEmpty()){
            String ext = nomFic.substring( nomFic.lastIndexOf( '/' ) + 1 ).substring( nomFic.lastIndexOf( '\\' ) + 1 ).substring(nomFic.lastIndexOf('.'));
            nomFic=idUser+ext ;
            this.ecrireFichier(part,nomFic,chemin);
        }

        SessionFactory factory = (SessionFactory)getServletContext().getAttribute(Init.ATT_SESSION_FACTORY);
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction() ;
        List<User> userr = (List<User>) session.createQuery("from User u where u.id= :idUser").
                                                setParameter("idUser",idUser).getResultList();
        User user1=userr.get(0);
        if(nomUser!=null)  user1.setFirstName(nomUser);
        if(prenomUser !=null) user1.setLastName(prenomUser);
        if(passUser != null) user1.setPassword(HelperFunctions.getSHA1(passUser));
        if(nomFic !=null) user1.setImg(nomFic);

        user1.setPhoneNumber(telUser);

        if(user1.getAddress() == null){
            session.save(address);
            user1.setAddress(address);
        }
        session.update(user1);
        transaction.commit();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonRespons = mapper.createObjectNode();
        jsonRespons.put("ok",true);
        jsonRespons.put("photo", nomFic);
        jsonRespons.put("firstName",nomUser);
        response.getWriter().print(jsonRespons.toString());


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }


    private static String getNomFichier( Part part ) {

        for ( String contentDisposition : part.getHeader( "content-disposition" ).split( ";" ) ) {
            if ( contentDisposition.trim().startsWith("filename") ) {
                return contentDisposition.substring( contentDisposition.indexOf( '=' ) + 1 ).trim().replace("\"","");
            }
        }
        return null;
    }


    private void ecrireFichier( Part part, String nomFichier, String chemin ) throws IOException {

       // new File(chemin+nomFichier).delete();
        BufferedInputStream entree = null;
        BufferedOutputStream sortie = null;

        try {
            entree = new BufferedInputStream(part.getInputStream(), TAILLE_TAMPON);
            File f = new File(chemin + nomFichier);
            f.canRead();
            f.canWrite();
            sortie = new BufferedOutputStream(new FileOutputStream(f),
                    TAILLE_TAMPON);
            assert entree != null && sortie != null;

            byte[] tampon = new byte[TAILLE_TAMPON];
            int longueur;
            while ((longueur = entree.read(tampon)) > 0) {
                sortie.write(tampon, 0, longueur);
            }
            entree.close(); sortie.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

