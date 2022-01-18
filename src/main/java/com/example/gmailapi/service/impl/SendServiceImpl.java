package com.example.gmailapi.service.impl;

import static com.example.gmailapi.common.Constants.USER_ME;

import com.example.gmailapi.dto.SendRequestDto;
import com.example.gmailapi.exception.SendServiceException;
import com.example.gmailapi.service.SendService;
import com.example.gmailapi.service.client.GmailService;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendServiceImpl implements SendService {
  private GmailService gmailService;

  public SendServiceImpl(GmailService gmailService) {
    this.gmailService = gmailService;
  }

  public String createEmail(SendRequestDto requestDto) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    MimeMessage email = new MimeMessage(session);
    try {
      email.setFrom(new InternetAddress(USER_ME));
      email.addRecipient(javax.mail.Message.RecipientType.TO,
              new InternetAddress(requestDto.getTo()));
      email.setSubject(requestDto.getSubject());
      email.setText(requestDto.getBodyText());
    } catch (MessagingException e) {
      log.error("Can't create mime message with param: to "
              + requestDto.getTo() + ", subject " + requestDto.getSubject());
      throw new SendServiceException("Can't create mime message with param: to "
              + requestDto.getTo() + ", subject " + requestDto.getSubject(), e);
    }
    return sendMessage(email);
  }

  private String sendMessage(MimeMessage email) {
    Gmail service = gmailService.getService();
    Message message = createMessageWithEmail(email);
    try {
      message = service.users().messages().send(USER_ME, message).execute();
      System.out.println("Message id: " + message.getId());
      System.out.println(message.toPrettyString());
      log.info("Successfully sending a message " + message.getId() + ". ");
      return "Message id: " + message.getId()
              + System.lineSeparator() + message.toPrettyString();
    } catch (IOException e) {
      log.error("Can't send message" + message.getId() + ". ");
      throw new SendServiceException("Can't send message. ", e);
    }
  }

  private Message createMessageWithEmail(MimeMessage email) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      email.writeTo(baos);
    } catch (IOException | MessagingException e) {
      log.error("Can't write to message to byte. ");
      throw new SendServiceException("Can't write to message to byte. ", e);
    }
    String encodedEmail = com.google.api.client.util.Base64.encodeBase64URLSafeString(baos.toByteArray());
    Message message = new Message();
    message.setRaw(encodedEmail);
    return message;
  }
}
