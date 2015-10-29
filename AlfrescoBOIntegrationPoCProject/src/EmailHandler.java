import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.*;

/**
 *
 * @author C5215816
 */
public class EmailHandler {

    private final Session session;
    private final String fromAddress;

    private static final String subject = "Alfresco-New report available";

    public EmailHandler(String SMTP_HOSTNAME, String SMTP_PORT, String FROM_ADDRESS) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOSTNAME);
        props.put("mail.debug", "false");
        props.put("mail.smtp.user", "prhua@agilesolutions.com");
        props.put("mail.smtp.pass", "Joya0804");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.setProperty("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable", "true");
        this.session = Session.getDefaultInstance(props, null);
        this.fromAddress = FROM_ADDRESS;
    }

    public void sendEmail(String destination, String password, String messageText) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        session.setDebug(false);
        message.setFrom(new InternetAddress(fromAddress));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(destination));
        message.setSubject(subject);
        
        message.setText(buildMessage(messageText));
        Transport.send(message, this.fromAddress,password); 
    }
    
    private String buildMessage(String messageText) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("JAVA program of Alfresco-BO integration\n")
                .append("\n")
                .append(messageText).append("\n")
                .append("\n")
                .append("Please do not reply this email.\n");
        return strBuilder.toString();
    }
    
    public static void main(String[] args) {
    	EmailHandler emailHandler = new EmailHandler("smtp.office365.com", "587", "prhua@agilesolutions.com");
    	try {
			emailHandler.sendEmail("alima@agilesolutions.com", "Joya0804", "Email teste");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}

}
