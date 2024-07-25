package com.git.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

//@Configuration
public class RestTemplateConfig {

//    @Value("${github.token}")
//    private String githubToken;
//
//    @Bean
//    public RestTemplate restTemplate() {
//        RestTemplate restTemplate = new RestTemplate();
//
//        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//        interceptors.add((request, body, execution) -> {
//            request.getHeaders().set("Authorization", "token" + githubToken);
//            return execution.execute(request, body);
//        });
//
//        restTemplate.setInterceptors(interceptors);
//        return restTemplate;
//    }
}
