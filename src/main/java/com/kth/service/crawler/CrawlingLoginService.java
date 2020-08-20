package com.kth.service.crawler;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@ConfigurationProperties(prefix = "crawler")
public class CrawlingLoginService {
    @Autowired
    private RestTemplate restTemplate;
    @Setter
    private String defaultUserAgent;

    /**
     * 로그인 처리를 실행 하고 cookie 정보를 반환
     * @param loginUrl
     * @param mvm
     * @return
     */
    public List<String> executeLogin(String loginUrl, LinkedMultiValueMap<String, Object> mvm) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("user-agent", defaultUserAgent);

        HttpEntity<LinkedMultiValueMap> requestEntity = new HttpEntity<>(mvm, headers);

        ResponseEntity<String> result = restTemplate.exchange(loginUrl, HttpMethod.POST, requestEntity, String.class);

        return result.getHeaders().get("Set-Cookie");
    }
}
