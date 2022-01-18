package com.example.gmailapi.controller;

import com.example.gmailapi.dto.SendRequestDto;
import com.example.gmailapi.service.impl.ReadServiceImpl;
import com.example.gmailapi.service.impl.SendServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/mail")
public class GmailApiController {
  private ReadServiceImpl readService;
  private SendServiceImpl sendService;

  public GmailApiController(ReadServiceImpl readService, SendServiceImpl sendService) {
    this.readService = readService;
    this.sendService = sendService;
  }

  @GetMapping("/get")
  public ResponseEntity<String> getMail(@RequestParam String sender) {
    log.trace("Received the sender parameters" + sender);
    return ResponseEntity.ok(readService.readFrom(sender));
  }

  @PostMapping("/send")
  public ResponseEntity<String> sendMail(@RequestBody SendRequestDto requestDto) {
    log.trace("Received information to send to " + requestDto.getTo() + " with subject" + requestDto.getSubject());
    return ResponseEntity.ok(sendService.createEmail(requestDto));
  }
}
