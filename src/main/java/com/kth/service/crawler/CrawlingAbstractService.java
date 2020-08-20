package com.kth.service.crawler;

import com.kth.model.crawler.Crawling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 크롤링 서비스를 위한 abstract class
 */
public abstract class CrawlingAbstractService implements CrawlingService {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${crawler.defaultUserAgent}")
    private String defaultUserAgent;

    /**
     * 상품 검색
     * @param searchUrl
     * @param cookies
     * @return
     */
    @Override
    public List<Crawling> execute(String searchUrl, List<String> cookies) {
        ResponseEntity<String> response = this.get(searchUrl, cookies);

        return response.getBody() == null ? new ArrayList<>() : this.getSearchResult(response.getBody());
    }

    /**
     * 상품 상세 검색
     * @param detailUrl
     * @param cookies
     * @return
     */
    @Override
    public Crawling executeDetail(String detailUrl, List<String> cookies) {
        ResponseEntity<String> response = this.get(detailUrl, cookies);

        return response.getBody() == null ? new Crawling() : this.getSearchDetail(response.getBody());
    }

    /**
     * Get Method로 url 실행
     * @param searchUrl
     * @param cookies
     * @return
     */
    private ResponseEntity<String> get(String url, List<String> cookies) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", defaultUserAgent);
        headers.addAll("Cookie", cookies);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    /**
     * 조회 결과를 반환
     * @param response
     * @return
     */
    public abstract List<Crawling> getSearchResult(String response);

    /**
     * 상세정보 조회 결과를 반환
     * @param response
     * @return
     */
    public abstract Crawling getSearchDetail(String response);
}
