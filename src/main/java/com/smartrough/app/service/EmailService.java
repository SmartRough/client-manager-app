package com.smartrough.app.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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
		configureCustomSSL();

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

	private static void configureCustomSSL() throws Exception {
		InputStream certStream = EmailService.class.getClassLoader().getResourceAsStream("certs/mail_com.crt");
		if (certStream == null) {
			throw new FileNotFoundException("No se encontró el certificado mail_com.crt en resources.");
		}

		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate cert = cf.generateCertificate(certStream);

		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null); // Nuevo keystore vacío
		ks.setCertificateEntry("mailcom", cert);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);

		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, tmf.getTrustManagers(), null);
		SSLContext.setDefault(ctx); // Hace que todas las conexiones usen esto
	}

}
