package sh.tiago.persistence_service;

import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.strategy.SegmentNamingStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

@SpringBootApplication
public class PersistenceServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PersistenceServiceApplication.class, args);
	}

	@Bean
	public Filter tracingFilter() {
		return new AWSXRayServletFilter(SegmentNamingStrategy.dynamic("persistence-service"));
	}

}
