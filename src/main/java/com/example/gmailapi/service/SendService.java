package com.example.gmailapi.service;

import com.example.gmailapi.dto.SendRequestDto;

public interface SendService {
  String createEmail(SendRequestDto requestDto);
}
