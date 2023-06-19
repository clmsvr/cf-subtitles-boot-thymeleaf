package cms.cf.test;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailApp
{
    public static void main(String[] args)
    {
        /* Parâmetros de conexão com servidor Gmail */
        //SSL
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

//        //TTLS
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
        
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication("cl.silveira@gmail.com", "eceJ2740&*");
            }
        });

        /* Ativa Debug para sessão */
        //session.setDebug(true);

        try
        {
            Message message = new MimeMessage(session);
            
            //Remetente
            message.setFrom(new InternetAddress("cl.silveira@gmail.com")); 

            //Destinatário(s)
            Address[] toUser = InternetAddress.parse("sudenio@yahoo.com, cl.silveira@gmail.com");
            message.setRecipients(Message.RecipientType.TO, toUser);
            //message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("sudenio@yahoo.com"));
            
            message.setSubject("Enviando email com JavaMail");
            message.setText("Enviei este email utilizando JavaMail com minha conta GMail!");

            Transport.send(message);
            System.out.println("Feito!!!");

//ou
//            Transport transport = session.getTransport("smtp");
//            transport.connect("smtp.gmail.com", "cl.silveira@gmail.com", "eceJ2740&*");
//            transport.sendMessage(message, message.getAllRecipients());
//            transport.close();            

        }
        catch (MessagingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
