package com.latish.cowinbooker.actions;

import com.latish.cowinbooker.models.SlotResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TelegramBotNotifier implements SlotObserver {

    final String TOKEN = "YOUR_BOT_TOKEN";
    final String CHAT_ID = "YOUR_CHAT_ID";
    final String API = "https://api.telegram.org";

    private static final RestTemplate restTemplate = new RestTemplate();

    public void onSlotAvailable(List<SlotResult> results) {
        final String message = makeNotificationMessage(results);

        UriBuilder builder = UriBuilder
                .fromUri(API)
                .path("/{token}/sendMessage")
                .queryParam("chat_id", CHAT_ID)
                .queryParam("text", message);

        ResponseEntity<String> response = restTemplate
                .getForEntity(builder.build("bot" + TOKEN), String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Notified user of available slots!");
        } else {
            log.error("Error while notifying the user, {}: {}", response.getStatusCode(), response.getBody());
        }
    }

    private String makeNotificationMessage(List<SlotResult> results) {
        return results.stream()
                .map(result -> String.format(
                        "%s, %s, %s has %d slots available, book fast!",
                        result.getName(),
                        result.getAddress(),
                        result.getBlock_name(),
                        result.getSessions().get(0).getAvailable_capacity()
                        )
                )
                .collect(Collectors.joining("\n\n"));
    }
}
