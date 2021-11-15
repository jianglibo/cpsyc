package me.resp.cpsyc;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleTest {
	
	@Test
	void tEncrypt() {

	log.info(JasyptUtil.encrypt(""));

	}
}
