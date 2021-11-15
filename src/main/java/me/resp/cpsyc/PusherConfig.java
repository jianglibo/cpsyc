package me.resp.cpsyc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "pusher")
@Getter
@Setter
public class PusherConfig {
	private String app_id;
	private String key;
	private String secret;
	private String cluster;
}
