package init;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by Zahir on 24/10/2016.
 */
public class MyMailSender {

    public static void envoyerMail() throws Exception{

        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            String message = "Ceci est un Test";

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("chelbi.zahir@gmail.com"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress("khelifa.berrefas@gmail.com"));
            msg.setSubject("Meteo Test!");
            msg.setText(message);
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
