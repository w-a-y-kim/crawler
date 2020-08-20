package com.kth.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * rest template를 반환
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        final HttpClient httpClient = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .disableCookieManagement()
            .build();
        RestTemplate result = new RestTemplate();
        result.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        return result;
    }
}
