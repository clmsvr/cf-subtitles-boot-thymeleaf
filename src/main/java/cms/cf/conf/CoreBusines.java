package cms.cf.conf;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import cms.cf.subtitles.dao.impl.ResetTokenDao;
import cms.cf.subtitles.dao.impl.RoleDao;
import cms.cf.subtitles.dao.impl.UserDao;
import cms.cf.subtitles.dao.impl.UserTempDao;
import cms.cf.subtitles.dao.vo.ResetToken;
import cms.cf.subtitles.dao.vo.User;
import cms.cf.subtitles.dao.vo.UserTemp;

public class CoreBusines
{
    private static CoreBusines instance = new CoreBusines();
    
    private Logger logger = Logger.getLogger(CoreBusines.class);
    
    private CoreBusines()
    {
    }
    
    public static CoreBusines geteInstance()
    {
        return instance;
    }
    
    public void confirmUser(UserTemp user)
    throws Exception
    {
        //gerar token para email de confirmacao
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPwd(user.getPwd());
        newUser.setName(user.getName());

        //Adicionar Usuario Temporario a espera da confirmacao
        Connection conn = null;
        try
        {
            conn = UserDao.getConnection();
            UserTempDao.delete(user.getToken());
            UserDao.insert(newUser);
            RoleDao.insert(user.getEmail(), "login");
            RoleDao.insert(user.getEmail(), "worker");
            conn.commit();
        }
        catch (Exception e)
        {
            logger.error("Falha confirmando novo usuario no banco.", e);
            throw e;
        }
        finally
        {
            UserDao.closeConnection(conn);
        }
    }
    
    public void regiterUser(User user)
    throws Exception
    {
        //gerar token para email de confirmacao
        String token = makeToken();
        UserTemp ut = new UserTemp(user,token);

        //Adicionar Usuario Temporario a espera da confirmacao
        try
        {
            UserTempDao.insert(ut);
        }
        catch (Exception e)
        {
            logger.error(e);
            throw e;
        }

        //Enviar email de confirma��o
        try
        {
            sendConfirmAccountEmail(ut);
        }
        catch (Exception e)
        {
            logger.error("ERRO ao enviar EMAIL de confirmcao ao usuario ["+user.getEmail()+"].",e);
        }
    }
    
    private void sendConfirmAccountEmail(UserTemp user)
    throws Exception
    {
        Properties p = ConfigHelper.getConfigFileProperties("mail.ini",true);            
        
        String sender = p.getProperty("mail.sender") ;
        String pwd = ConfigHelper.decode64(p.getProperty("mail.pwd"));
        String url = p.getProperty("mail.confirm.url");
        
        if (sender == null || pwd == null) throw new Exception("Erro lendo credenciais para enviar EMAILs"); 
        
        /* Par�metros de conex�o com servidor Gmail */
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
                return new PasswordAuthentication(sender,pwd);
            }
        });

        /* Ativa Debug para sess�o */
        //session.setDebug(true);

        Message message = new MimeMessage(session);
        
        //Remetente
        message.setFrom(new InternetAddress(sender)); 

        //Destinat�rio(s)
        //Address[] toUser = InternetAddress.parse("sudenio@yahoo.com, cl.silveira@gmail.com");
        //message.setRecipients(Message.RecipientType.TO, toUser);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
        
        message.setSubject("Caio Fabio - Legendas - Nova Conta.");
        message.setText("Confirma��o do email do cadasstro. <a href='"+url+"?token="+user.getToken()+"'>Link</a>");

        Transport.send(message);
//ou
//        Transport transport = session.getTransport("smtp");
//        transport.connect("smtp.gmail.com", "cl.silveira@gmail.com", "eceJ2740&*");
//        transport.sendMessage(message, message.getAllRecipients());
//        transport.close();            
        
    }    
    
    private void sendResetPwdEmail(ResetToken rt)
    throws Exception
    {
        Properties p = ConfigHelper.getConfigFileProperties("mail.ini",true);            
        
        String sender = p.getProperty("mail.sender") ;
        String pwd = ConfigHelper.decode64(p.getProperty("mail.pwd"));
        String url = p.getProperty("mail.reset-confirm.url");
        
        if (sender == null || pwd == null) throw new Exception("Erro lendo credenciais para enviar EMAILs"); 
        
        /* Par�metros de conex�o com servidor Gmail */
        //SSL
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(sender,pwd);
            }
        });

        Message message = new MimeMessage(session);
        
        //Remetente
        message.setFrom(new InternetAddress(sender)); 

        //Destinat�rio(s)
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rt.getEmail()));
        
        message.setSubject("Caio Fabio - Legendas");
        message.setText("Link para atualiza��o de sua senha. <a href='"+url+"?token="+rt.getToken()+"'>Link</a>");

        Transport.send(message);
    }    
   
    private String makeToken()
    {
        return makeToken2();
    }
    
    static final String AB  = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&*()_+-=[]{}<>:,.;?/|";
    static SecureRandom rnd = new SecureRandom();
    
    @SuppressWarnings("unused")
    private String makeToken1(int len)
    {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
    
    private String makeToken2()
    {
        return new BigInteger(130, rnd).toString(32);
    }
    
    
    public void sendResetPwdEmail(String email)
    throws Exception
    {
        //gerar token para email de confirmacao
        String token = makeToken();
       
        ResetToken rt = new ResetToken();
        rt.setEmail(email);
        rt.setToken(token);

        //Adicionar Usuario Temporario a espera da confirmacao
        ResetTokenDao.insert(rt);

        //Enviar email de confirma��o
        sendResetPwdEmail(rt);
    }
    
    private CleanUp cleanUp;
    public void startCleanUpThread() throws Exception
    {
        cleanUp = new CleanUp();
    }
    
    public void stopCleanUpThread()
    {
        if (cleanUp != null) cleanUp.close();
    }
}
