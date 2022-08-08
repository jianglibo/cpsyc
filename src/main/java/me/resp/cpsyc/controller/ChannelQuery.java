package me.resp.cpsyc.controller;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pusher.rest.Pusher;

import me.resp.cpsyc.CpsycConstants;
import me.resp.cpsyc.FaunaUtil;
import me.resp.cpsyc.dto.ChannelData;

@RestController
@RequestMapping("/channel")
public class ChannelQuery {

  private final Pusher pusher;

  private final FaunaUtil faunaUtil;

  /** @param pusher */
  public ChannelQuery(Pusher pusher, FaunaUtil faunaUtil) {
    this.pusher = pusher;
    this.faunaUtil = faunaUtil;
  }

  public static int getRandomNumberUsingNextInt(int min, int max) {
    Random random = new Random();
    return random.nextInt(max - min) + min;
  }

  public static int nextInt() {
    return getRandomNumberUsingNextInt(100000000, 999999999);
  }

  @CrossOrigin
  @GetMapping(path = "/query")
  public ChannelData getBook(@RequestParam String channel_name)
      throws InterruptedException, ExecutionException {
    if (channel_name == null || channel_name.trim().isEmpty()) {
      String new_channel_name = nextInt() + "";
      ChannelData cd = ChannelData.builder().id(new_channel_name).content("").build();
      return faunaUtil.insertChannelData(cd);
    } else {
      Optional<ChannelData> cd = faunaUtil.byId(channel_name);
      if (cd.isPresent()) {
        return cd.get();
      } else { // warn user.
        return ChannelData.builder()
            .id("no_channel")
            .content("")
            .created_at(CpsycConstants.isoDateTimeString())
            .build();
      }
    }
  }
}
