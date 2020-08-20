package com.kth.service.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * crawler factory
 */
@Service
public class CrawlerFactory {
    @Autowired
    private List<CrawlingService> serviceList;
    /** service mapping hash map */
    private Map<String, CrawlingService> serviceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for(CrawlingService item : serviceList) {
            serviceMap.put(item.getServiceId(), item);
        }
    }

    /**
     * instance를 반환
     * @param serviceId
     * @return
     */
    public CrawlingService getInstance(String serviceId) {
        return serviceMap.get(serviceId);
    }
}
