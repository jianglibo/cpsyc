package me.resp.cpsyc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AfterApplicationReady {

  @Autowired private PusherConfig pusherConfig;

  @EventListener(ApplicationReadyEvent.class)
  public void after() {
	log.info("the pusher's key is: " + pusherConfig.getKey());
  }
}
