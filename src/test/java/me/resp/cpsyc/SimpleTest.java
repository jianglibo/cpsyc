package me.resp.cpsyc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import me.resp.cpsyc.controller.ChannelQuery;

@Slf4j
public class SimpleTest {

  @Test
  void tEncrypt() {
    log.info(JasyptUtil.encrypt(""));
  }

  @Test
  void tDecrypt() {
    log.info(
        JasyptUtil.decrypt(
            "ENC(QpuKlwqx2Mwwe+jSWdqUR3SXNxCF+8ERYpXkzD9+gvXGLJIw6wQCoelk+14ItLc+LudmM7qDV00SpMRjAAl1sMCZk5lv7DEyZuzDCZ4IdD0=)"));
  }

  @Test
  void tRandomInt() {
    int i = ChannelQuery.nextInt();
    log.info("int: {}", i);
    assertThat(i).isGreaterThan(100000000).isLessThan(999999999);
  }

  @Test
  void decode() {
    log.info(new String(Base64.getDecoder().decode("")));
  }
}
