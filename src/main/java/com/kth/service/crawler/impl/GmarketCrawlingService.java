package com.kth.service.crawler.impl;

import com.kth.model.crawler.Crawling;
import com.kth.service.crawler.CrawlingLoginService;
import com.kth.service.crawler.CrawlingSeleniumAbstractService;
import com.kth.service.crawler.CrawlingService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
 * gmarket 검색
 */
@Slf4j
@Service
@Validated
@ConfigurationProperties(prefix = "crawler.gmarket")
public class GmarketCrawlingService extends CrawlingSeleniumAbstractService implements CrawlingService {
    /** service id */
    @Setter @Getter
    private String serviceId;
    /** 메인 url */
    @Setter @Getter
    private String mainUrl;
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
    @Cacheable(cacheNames = "crawler:login:gmarket")
    public List<String> login() {
        log.debug("login");
        LinkedMultiValueMap<String, Object> mvm = new LinkedMultiValueMap<>();
        mvm.add("id", userId);
        mvm.add("pwd", password);
        mvm.add("command", "login");
        mvm.add("url", "http://www.gmarket.co.kr/");
        mvm.add("member_type", "MEM");
        mvm.add("keyFlag", "off");
        mvm.add("member_yn", "Y");

        return crawlingLoginService.executeLogin(loginUrl, mvm);
    }

    /**
     * cache clear
     */
    @Override
    @CacheEvict(value = "crawler:login:gmarket")
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
    public List<Crawling> getSearchResult(WebDriver webDriver) {
        List<Crawling> resultList = new ArrayList<>();
        List<WebElement> elements = webDriver.findElements(By.cssSelector(".item_list li"));
        for(WebElement item : elements) {
            String imgPath = item.findElement(By.cssSelector(".item_info img")).getAttribute("src");
            String productName = item.findElement(By.cssSelector(".item_info .title")).getText();
            String detailLink = item.findElement(By.cssSelector(".item_info a")).getAttribute("href");
            String price = item.findElement(By.cssSelector(".price")).getText();

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
    public Crawling getSearchDetailResult(WebDriver webDriver) {
        Crawling result = new Crawling();

        String imgPath = webDriver.findElement(By.cssSelector(".viewer .on a img")).getAttribute("src");
        String productName = webDriver.findElement(By.cssSelector(".itemtit")).getText();
        String price = webDriver.findElement(By.cssSelector(".price_real")).getText();

        result.setImgPath(imgPath);
        result.setProductName(productName);
        result.setPrice(price);

        return result;
    }
}
