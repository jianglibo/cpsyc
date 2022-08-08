package me.resp.cpsyc.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Home {


  @CrossOrigin
  @GetMapping(path = "", produces = MediaType.TEXT_HTML_VALUE)
  @ResponseBody
  public String getBook() {
    return "It works!";
  }
}
