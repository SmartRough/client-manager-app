package com.smartrough.app.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class EmailService {

	private static final Properties config = new Properties();

	static {
		try (InputStream in = EmailService.class.getClassLoader().getResourceAsStream("config/email.properties")) {
			if (in == null)
				throw new FileNotFoundException("config/email.properties not found in classpath.");
			config.load(in);
		} catch (Exception e) {
			throw new RuntimeException("Could not load email.properties", e);
		}
	}

	public static void sendEmail(String to, String subject, String body, List<File> attachments) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.auth", config.getProperty("smtp.auth"));
		props.put("mail.smtp.starttls.enable", config.getProperty("smtp.starttls"));
		props.put("mail.smtp.host", config.getProperty("smtp.host"));
		props.put("mail.smtp.port", config.getProperty("smtp.port"));

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(config.getProperty("smtp.username"),
						config.getProperty("smtp.password"));
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(config.getProperty("smtp.username")));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		message.setSubject(subject);

		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText(body);

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(textPart);

		for (File file : attachments) {
			MimeBodyPart attachPart = new MimeBodyPart();
			attachPart.attachFile(file);
			multipart.addBodyPart(attachPart);
		}

		message.setContent(multipart);
		Transport.send(message);
	}
}
