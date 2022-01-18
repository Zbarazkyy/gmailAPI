package com.example.gmailapi.common;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

public class Constants {
  public static final String APPLICATION_NAME = "Gmail API Java Quickstart";
  public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  public static final String USER_ME = "me";
}
