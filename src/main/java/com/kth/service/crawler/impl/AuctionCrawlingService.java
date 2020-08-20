package com.kth.service.crawler.impl;

import com.kth.model.crawler.Crawling;
import com.kth.service.crawler.CrawlingAbstractService;
import com.kth.service.crawler.CrawlingLoginService;
import com.kth.service.crawler.CrawlingService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * action 검색
 */
@Slf4j
@Service
@Validated
@ConfigurationProperties(prefix = "crawler.auction")
public class AuctionCrawlingService extends CrawlingAbstractService implements CrawlingService {
    /** service id */
    @Setter @Getter
    private String serviceId;
    /** 로그인 URL */
    @Setter
    private String loginUrl;
    /** 로그인 유저 id */
    @Setter
    @NotBlank
    private String userId;
    /** 비밀번호 */
    @Setter
    @NotBlank
    private String password;
    @Autowired
    private CrawlingLoginService crawlingLoginService;

    /**
     * 로그인
     * @return
     */
    @Override
    @Cacheable(cacheNames = "crawler:login:auction")
    public List<String> login() {
        log.debug("login");
        LinkedMultiValueMap<String, Object> mvm = new LinkedMultiValueMap<>();
        mvm.add("id", userId);
        mvm.add("password", password);

        return crawlingLoginService.executeLogin(loginUrl, mvm);
    }

    /**
     * cache clear
     */
    @Override
    @CacheEvict(value = "crawler:login:auction", allEntries = true)
    @Scheduled(fixedDelay = 20 * 60 * 1000)
    public void cacheClear() {
        log.debug("login cache clear");
    }

    /**
     * 조회 결과를 parsing하여 반환
     * @param response
     * @return
     */
    @Override
    public List<Crawling> getSearchResult(String response) {
        List<Crawling> resultList = new ArrayList<>();
        Document doc = Jsoup.parse(response);
        Elements elements = doc.select(".component--item_card");
        for(Element item : elements) {
            String imgPath = item.select(".section--itemcard_img img").attr("src");
            String productName = item.select(".section--itemcard_info_major .text--title").text();
            String detailLink = item.select(".section--itemcard_img a").attr("href");
            String price = item.select(".text--price_seller").text();

            Crawling result = new Crawling();
            result.setImgPath(imgPath);
            result.setProductName(productName);
            result.setDetailLink(detailLink);
            result.setPrice(price);
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * 상세 조회결과를 parsing하여 반환
     * @param response
     * @return
     */
    @Override
    public Crawling getSearchDetail(String response) {
        Crawling result = new Crawling();

        Document doc = Jsoup.parse(response);
        String imgPath = doc.select(".viewerwrap .viewer .on a img").attr("src");
        String productName = doc.select(".itemtit").text();
        String price = doc.select(".price_real").text();

        result.setImgPath(imgPath);
        result.setProductName(productName);
        result.setPrice(price);

        return result;
    }
}
