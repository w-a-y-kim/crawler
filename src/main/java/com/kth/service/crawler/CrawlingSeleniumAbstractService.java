package com.kth.service.crawler;

import com.kth.model.crawler.Crawling;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 크롤링 서비스를 위한 abstract class
 */
@Slf4j
public abstract class CrawlingSeleniumAbstractService implements CrawlingService {
    /** driver path */
    @Value("${crawler.driverPath}")
    private String driverPath;

    @PostConstruct
    public void init() {
        System.setProperty("webdriver.chrome.driver", driverPath);
    }

    /**
     * 상품 검색
     * @param detailUrl
     * @param cookies
     * @return
     */
    @Override
    public List<Crawling> execute(String detailUrl, List<String> cookies) {
        WebDriver webDriver = null;
        try{
            webDriver = this.executeSelenium(detailUrl, cookies);

            return this.getSearchResult(webDriver);
        }catch(Exception e) {
            log.error("executeDetail exception detailUrl[{}]", detailUrl);
            throw new RuntimeException(e);
        }finally {
            if(webDriver != null) {
                webDriver.quit();
            }
        }
    }

    /**
     * 상품 상세 검색
     * @param detailUrl
     * @param cookies
     * @return
     */
    @Override
    public Crawling executeDetail(String detailUrl, List<String> cookies) {
        WebDriver webDriver = null;
        try{
            webDriver = this.executeSelenium(detailUrl, cookies);

            return this.getSearchDetailResult(webDriver);
        }catch(Exception e) {
            log.error("executeDetail exception detailUrl[{}]", detailUrl);
            throw new RuntimeException(e);
        }finally {
            if(webDriver != null) {
                webDriver.quit();
            }
        }
    }

    /**
     * web driver를 실행
     * @param detailUrl
     * @param cookies
     * @return
     */
    private WebDriver executeSelenium(String executeUrl, List<String> cookies) {
        String mainUrl = this.getMainUrl();
        WebDriver webDriver = null;
        try {
            URL url = new URL(mainUrl);
            String cookieDomain = "." + StringUtils.replace(url.getHost(), "www.", "");
            webDriver = this.chromeDriver();
            WebDriverWait wait = new WebDriverWait(webDriver, 60 * 5);

            // cookie 설정을 위해 메인 페이지 호출
            webDriver.get(mainUrl);
            wait.until(driver -> (boolean)((JavascriptExecutor)driver).executeScript("return document.readyState == 'complete'"));

            // 로그인 정보를 설정
            this.setCookie(webDriver, cookies, cookieDomain);
            webDriver.get(executeUrl);

            wait.until(driver -> (boolean)((JavascriptExecutor)driver).executeScript("return document.readyState == 'complete'"));

            return webDriver;

        }catch(Exception e) {
            log.error("executeDetail exception url[{}]", executeUrl);
            if(webDriver != null) {
                webDriver.quit();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 로그인 쿠키정보를 설정한다.
     * @param webDriver
     * @param cookies
     * @param domain
     */
    private void setCookie(WebDriver webDriver, List<String> cookies, String domain) {
        webDriver.manage().deleteAllCookies();
        for (String item : cookies) {
            List<java.net.HttpCookie> parsingCookies = java.net.HttpCookie.parse(item);
            if (parsingCookies.size() == 0) {
                continue;
            }
            java.net.HttpCookie cookie = parsingCookies.get(0);
            webDriver.manage().addCookie(new Cookie.Builder(cookie.getName(), cookie.getValue()).domain(domain).path(cookie.getPath()).build());
        }
    }

    /**
     * chrome driver를 반환
     * @return
     */
    private WebDriver chromeDriver() {
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("headless");
        options.addArguments("--disable-images");
        options.addArguments("--window-size=1920,1080");

        WebDriver webDriver = null;
        try {
            webDriver = new ChromeDriver(options);
            webDriver.manage().timeouts().pageLoadTimeout(60 * 5, TimeUnit.SECONDS);
            return webDriver;
        } catch(Exception e) {
            if(webDriver != null) {
                webDriver.quit();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 메인페이지 url
     * @return
     */
    protected abstract String getMainUrl();

    /**
     * 조회 결과를 parsing하여 반환
     * @param response
     * @return
     */
    public abstract List<Crawling> getSearchResult(WebDriver webDriver);

    /**
     * 상품 상세 조회 결과
     * @param webDriver
     * @return
     */
    public abstract Crawling getSearchDetailResult(WebDriver webDriver);
}
