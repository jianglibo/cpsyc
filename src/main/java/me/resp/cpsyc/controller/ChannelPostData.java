package me.resp.cpsyc.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.pusher.rest.Pusher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.resp.cpsyc.FaunaUtil;

@RestController
@RequestMapping("/channel")
public class ChannelPostData {
  private final Pusher pusher;
  private final FaunaUtil faunaUtil;

  /** @param pusher */
  @Autowired
  public ChannelPostData(Pusher pusher, FaunaUtil faunaUtil) {
    this.pusher = pusher;
    this.faunaUtil = faunaUtil;
  }

  @CrossOrigin
  @PostMapping(path = "/post")
  public String getBook(@RequestBody Map<String, Object> channel_data)
      throws InterruptedException, ExecutionException {
    String id = channel_data.remove("id").toString().replaceAll("[^0-9]", "");
    String socket_id = channel_data.remove("socket_id").toString();
    faunaUtil.update(id, channel_data);

    Map<String, Object> pushContent = new HashMap<>();
    pushContent.put("socket_id", socket_id);
    pushContent.put("content", channel_data.get("content"));
    pusher.trigger("private-" + id, "contentUpdated", pushContent);
    return "";
  }
}
