package me.resp.cpsyc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CpsycApplicationTests {
	@Autowired
	private PusherConfig pusherConfig;
	@Test
	void contextLoads() {
		assertThat(pusherConfig.getApp_id()).isEqualTo("1297074");
	}

}
