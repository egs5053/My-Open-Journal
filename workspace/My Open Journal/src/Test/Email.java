package Test;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

//import org.apache.catalina.Session;

public class Email {
	public static  void SendWelcomeEmail(String toAddress) {    
	      // Recipient's email ID needs to be mentioned.
	      String to = toAddress;

	      // Sender's email ID needs to be mentioned
	      String from = "admin@myopenjournal.org";

	      // Assuming you are sending email from localhost
	      String host = "cluster1.us.messagelabs.com";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);

	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);

	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject("Welcome to My Open Journal!");

	         // Now set the actual message
	         message.setText("We hope you find My Open Journal to be useful.\n\n" +
	        		 "To get started and submit your first paper, click here:\n" +
	        		 "www.myopenjournal.org/upload.zul\n\n" +
	        		 "If you run into any issues, please submit a bug report " +
	        		 "at the following link: \n http://www.google.com/moderator/#16/e=209656");

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	}
	   
	public static  void SendPasswordReset(String toAddress, int resetCode) {    
	      // Recipient's email ID needs to be mentioned.
	      String to = toAddress;

	      // Sender's email ID needs to be mentioned
	      String from = "admin@myopenjournal.org";

	      // Assuming you are sending email from localhost
	      String host = "cluster1.us.messagelabs.com";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);

	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);

	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject("Welcome to My Open Journal!");

	         String resetString = String.valueOf(resetCode);
	         
	         // Now set the actual message
	         message.setText("The following is your password reset code:.\n\n" +
	        		 resetString + "\n\n Please enter it in the field to reset your password.");

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	      
	}
	
}
