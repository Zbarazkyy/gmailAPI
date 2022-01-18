package com.example.gmailapi.service.client;

import static com.example.gmailapi.common.Constants.APPLICATION_NAME;
import static com.example.gmailapi.common.Constants.JSON_FACTORY;
import static com.example.gmailapi.util.Util.CLIENT_ID;
import static com.example.gmailapi.util.Util.CLIENT_SECRET;
import static com.example.gmailapi.util.Util.FILE_PATH;
import static com.example.gmailapi.util.Util.REFRESH_TOKEN;
import static com.example.gmailapi.util.Util.URL_AUTH_TOKEN;

import com.example.gmailapi.exception.GmailServiceException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GmailService {
  public Gmail getService() {
    Gmail service = null;
    InputStream in = null;
    GoogleClientSecrets clientSecrets = null;
    Credential authorize = null;
    final NetHttpTransport HTTP_TRANSPORT;
    try {
      in = new FileInputStream(FILE_PATH);
      clientSecrets = GoogleClientSecrets.load(
              JSON_FACTORY, new InputStreamReader(in));
      authorize = new GoogleCredential.Builder()
              .setTransport(GoogleNetHttpTransport.newTrustedTransport())
              .setJsonFactory(JSON_FACTORY)
              .setClientSecrets(clientSecrets.getDetails().getClientId().toString(),
                      clientSecrets.getDetails().getClientSecret().toString())
              .build()
              .setAccessToken(getAccessToken())
              .setRefreshToken(REFRESH_TOKEN);
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    } catch (GeneralSecurityException | IOException e) {
      log.error("problem with credentials or can't read "
              + FILE_PATH.getName() + " ");
      throw new GmailServiceException(
              "Some problem with credentials or can't read", e);
    }
    service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize)
            .setApplicationName(APPLICATION_NAME).build();
    log.info("Successfully receiving gmail service. ");
    return service;
  }

  private String getAccessToken() {
    try {
      Map<String, Object> params = new LinkedHashMap<>();
      params.put("grant_type", "refresh_token");
      params.put("client_id", CLIENT_ID);
      params.put("client_secret", CLIENT_SECRET);
      params.put("refresh_token", REFRESH_TOKEN);
      StringBuilder postData = new StringBuilder();
      for (Map.Entry<String, Object> param : params.entrySet()) {
        if (postData.length() != 0) {
          postData.append('&');
        }
        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(
                String.valueOf(param.getValue()), "UTF-8"));
      }
      byte[] postDataBytes = postData.toString().getBytes("UTF-8");
      URL url = new URL(URL_AUTH_TOKEN);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setDoOutput(true);
      con.setUseCaches(false);
      con.setRequestMethod("POST");
      con.getOutputStream().write(postDataBytes);
      BufferedReader reader = new BufferedReader(
              new InputStreamReader(con.getInputStream()));
      StringBuffer buffer = new StringBuffer();
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        buffer.append(line);
      }
      JSONObject json = new JSONObject(buffer.toString());
      log.trace("Successfully receiving access token. ");
      return json.getString("access_token");
    } catch (Exception ex) {
      log.error("Can't get access token with client id " + CLIENT_ID + " ");
      throw new RuntimeException("Can't get access token. ", ex);
    }
  }
}
