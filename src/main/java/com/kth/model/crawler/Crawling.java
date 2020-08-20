package com.kth.model.crawler;

import lombok.Data;

import java.util.List;

/**
 * crawling obj
 */
@Data
public class Crawling {
    /** 이미지 경로 */
    private String imgPath;
    /** 상품명 */
    private String productName;
    /** 상세 링크 */
    private String detailLink;
    /** 가격 */
    private String price;
}
