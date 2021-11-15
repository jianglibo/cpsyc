package me.resp.cpsyc;



import com.pusher.rest.Pusher;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEncryptableProperties
public class CpsycApplication {
	public static void main(String[] args) {
		SpringApplication.run(CpsycApplication.class, args);
	}

	@Bean
	public Pusher publish(PusherConfig pusherConfig) {
		Pusher pusher = new Pusher(pusherConfig.getApp_id(),pusherConfig.getKey(),pusherConfig.getSecret());
		pusher.setCluster(pusherConfig.getCluster());
		return pusher;
	}

}
