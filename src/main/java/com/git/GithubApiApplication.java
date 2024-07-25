package com.git;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class GithubApiApplication {

	@Value("${github.token}")
	private String githubToken;

	public static void main(String[] args) {
		SpringApplication.run(GithubApiApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add((request, body, execution) -> {
			request.getHeaders().set("Authorization", "token " + githubToken);
			return execution.execute(request, body);
		});

		restTemplate.setInterceptors(interceptors);
		return restTemplate;
//		return new RestTemplate();
	}
}
