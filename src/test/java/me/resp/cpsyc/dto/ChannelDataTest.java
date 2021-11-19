package me.resp.cpsyc.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelDataTest {

  @Test
  void tCreate() {
    ChannelData cd =
        ChannelData.builder().id("1").content("content").build();
    assertThat(cd).hasNoNullFieldsOrPropertiesExcept("created_at");
    log.info("dt: {}", cd.getCreated_at());
  }
}
