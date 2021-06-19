package com.latish.cowinbooker.scheduler;

import com.latish.cowinbooker.actions.SlotObserver;
import com.latish.cowinbooker.models.SlotResult;
import com.latish.cowinbooker.models.SlotResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class SlotChecker {

    @Autowired
    private SlotObserver observer;

    @Value("${district.id}")
    private int districtId;

    private static final HttpHeaders headers = new HttpHeaders();
    private static final RestTemplate restTemplate = new RestTemplate();

    static {
        headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
        headers.add(HttpHeaders.REFERER, "https://selfregistration.cowin.gov.in/");
        headers.add(HttpHeaders.ORIGIN, "https://selfregistration.cowin.gov.in/");
    }

    @Scheduled(fixedDelay = 18_0_000)
    public void checkForAvailableSlots() {
        final HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);

        String tomorrow = LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(1)
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        log.info("Checking for slot availability for date {} for district id {}", tomorrow, districtId);

        final UriBuilder builder = UriBuilder
                .fromUri("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict")
                .queryParam("district_id", districtId)
                .queryParam("date", tomorrow);

        ResponseEntity<SlotResults> responseEntity = restTemplate.exchange(
                builder.build(),
                HttpMethod.GET,
                entity,
                SlotResults.class
        );

        SlotResults results = responseEntity.getBody();

        // filter the available slots
        List<SlotResult> availableSlots = null;

        if (results != null) {
            availableSlots = results.getCenters().stream()
                    .filter(this::isSlotAvailable)
                    .collect(Collectors.toList());

            log.info("{} slots after filtering...", availableSlots.size());
        }

        if (availableSlots != null && availableSlots.size() > 0) {
            log.info("{} slots available, notifying observers", availableSlots.size());
            observer.onSlotAvailable(availableSlots);
        }

    }

    private boolean isSlotAvailable(SlotResult result) {
        return result.getSessions().stream()
                .anyMatch(session -> session.getAvailable_capacity() > 0 && session.getMin_age_limit() >= 18);
    }
}
