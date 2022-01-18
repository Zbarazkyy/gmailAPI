package com.example.gmailapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendRequestDto {
  private String to;
  private String subject;
  private String bodyText;
}
