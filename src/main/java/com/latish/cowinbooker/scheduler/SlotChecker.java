package com.latish.cowinbooker.scheduler;

import com.latish.cowinbooker.constant.CowinURI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class SlotChecker {

    private static final HttpHeaders headers = new HttpHeaders();
    private static final RestTemplate restTemplate = new RestTemplate();

    static {
        headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
        headers.add(HttpHeaders.REFERER, "https://selfregistration.cowin.gov.in/");
        headers.add(HttpHeaders.ORIGIN, "https://selfregistration.cowin.gov.in/");
    }

    @Scheduled(fixedDelay = 18_0_000)
    public void checkForAvailableSlots() {
        log.info("Checking for slot availability..");
        final HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict" + URLEncoder.encode("?district_id=4&date=14-06-2021", StandardCharsets.UTF_8),
                entity,
                String.class
        );

        log.info(responseEntity.getBody());
    }
}
