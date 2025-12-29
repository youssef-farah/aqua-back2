package com.example.aqua.mail;

public interface EmailService {

	
    void sendEmail(String to, String subject, String text);

}
