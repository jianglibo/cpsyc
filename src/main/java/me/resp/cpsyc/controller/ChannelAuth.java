package me.resp.cpsyc.controller;

import com.pusher.rest.Pusher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/channel")
public class ChannelAuth {

  private final Pusher pusher;

  /**
   * @param pusher
   */
  @Autowired
  public ChannelAuth(Pusher pusher) {
    this.pusher = pusher;
  }

  @CrossOrigin
  @PostMapping(path = "/auth")
  public String getBook(@RequestParam String socket_id, @RequestParam String channel_name) {
    String a = pusher.authenticate(socket_id, channel_name);
    return a;
  }
}
