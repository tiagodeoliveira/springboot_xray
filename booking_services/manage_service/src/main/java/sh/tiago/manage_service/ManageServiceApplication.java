package sh.tiago.manage_service;

import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.strategy.SegmentNamingStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.servlet.Filter;

@SpringBootApplication
public class ManageServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ManageServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public Filter tracingFilter() {
		return new AWSXRayServletFilter(SegmentNamingStrategy.dynamic("manage-service"));
	}

}
