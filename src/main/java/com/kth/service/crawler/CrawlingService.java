package com.kth.service.crawler;

import com.kth.model.crawler.Crawling;

import java.util.List;

/**
 * 크롤링 서비스를 위한 interface
 */
public interface CrawlingService {
    /**
     * 로그인
     * @return
     */
    List<String> login();

    /**
     * 상품 검색
     * @param searchUrl
     * @param cookies
     * @return
     */
    List<Crawling> execute(String searchUrl, List<String> cookies);

    /**
     * 상품 상세 검색
     * @param detailUrl
     * @param cookies
     * @return
     */
    Crawling executeDetail(String detailUrl, List<String> cookies);

    /**
     * 캐시 초기화
     */
    void cacheClear();

    /**
     * serviceId 반환
     * @return
     */
    String getServiceId();
}
