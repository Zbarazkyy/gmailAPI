package com.example.gmailapi.service.impl;

import static com.example.gmailapi.common.Constants.USER_ME;

import com.example.gmailapi.exception.ReadServiceException;
import com.example.gmailapi.service.ReadService;
import com.example.gmailapi.service.client.GmailService;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReadServiceImpl implements ReadService {
  private GmailService gmailService;

  public ReadServiceImpl(GmailService gmailService) {
    this.gmailService = gmailService;
  }

  public String readFrom(String from) {
    Gmail service = gmailService.getService();
    Gmail.Users.Messages.List request = null;
    ListMessagesResponse messagesResponse = null;
    try {
      request = service.users().messages()
              .list(USER_ME).setQ("from: " + from);
      messagesResponse = request.execute();
    } catch (IOException e) {
      log.error("Can't get response from mail with param "
              + from + ". ");
      throw new ReadServiceException(
              "Can't get response from mail with param" + from + ". ", e);
    }
    request.setPageToken(messagesResponse.getNextPageToken());
    String messageId = null;
    try {
      messageId = messagesResponse.getMessages().get(0).getId();
    } catch (NullPointerException npe) {
      log.error("There are no letters with this sender: " + from + ". ");
      throw new ReadServiceException(
              "There are no letters with this sender: " + from + ". ", npe);
    }
    Message message = null;
    try {
      message = service.users().messages().get(USER_ME, messageId).execute();
    } catch (IOException e) {
      log.error("Can't get ID of the email for sender: " + from + ". ");
      throw new ReadServiceException("Can't get ID of the email. ", e);
    }
    String emailBody = StringUtils
            .newStringUtf8(Base64.decodeBase64
                    (message.getPayload().getParts().get(0).getBody().getData()));
    log.info("Successfully received mail by sender " + from + ". ");
    return emailBody;
  }
}
