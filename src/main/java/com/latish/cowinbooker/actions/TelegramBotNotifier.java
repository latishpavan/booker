package com.latish.cowinbooker.actions;

import com.latish.cowinbooker.models.SlotResult;
import com.latish.cowinbooker.models.telegram.Message;
import com.latish.cowinbooker.models.telegram.Update;
import com.latish.cowinbooker.models.telegram.Updates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TelegramBotNotifier implements SlotObserver {

    final String CHAT_ID = "your_chat";
    final String API = "https://api.telegram.org";
    final String TOKEN = "your_bot";

    private static final Set<Integer> mutedIds = new HashSet<>();
    private static final RestTemplate restTemplate = new RestTemplate();

    public void onSlotAvailable(List<SlotResult> results) {
        checkForUpdates();

        final String message = makeNotificationMessage(results);
        notify(message);
    }

    private void checkForUpdates() {
        final UriBuilder builder = UriBuilder
                .fromUri(API)
                .path("/{token}/getUpdates");

        final ResponseEntity<Updates> response = restTemplate.postForEntity(
                    builder.build("bot" + TOKEN),
                    null,
                    Updates.class
                );

        if (response.getStatusCode() == HttpStatus.OK) {
            final Updates updates = response.getBody();
            assert updates != null;

            // sort in ascending order by date
            final List<Message> messages = updates.getResult()
                    .stream()
                    .map(Update::getMessage)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(Message::getDate))
                    .collect(Collectors.toList());

            for (Message message : messages) {
                if (message.getText().matches("^mute \\d+")) {
                    Pattern pattern = Pattern.compile("^mute (\\d+)");
                    Matcher matcher = pattern.matcher(message.getText());

                    if (matcher.find()) {
                        int centerId = Integer.parseInt(matcher.group(1));
                        mutedIds.add(centerId);
                    }
                }
            }
        }
    }

    private void notify(String message) {
        if (message.length() == 0) {
            log.info("Not notifying the user as message length is zero");
            return;
        };

        final UriBuilder builder = UriBuilder
                .fromUri(API)
                .path("/{token}/sendMessage")
                .queryParam("chat_id", CHAT_ID)
                .queryParam("text", message);

        final ResponseEntity<String> response = restTemplate
                .getForEntity(builder.build("bot" + TOKEN), String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Notified user of available slots!");
        } else {
            log.error("Error while notifying the user, {}: {}", response.getStatusCode(), response.getBody());
        }
    }

    private String makeNotificationMessage(List<SlotResult> results) {
        return results.stream()
                .filter(result -> !mutedIds.contains(result.getCenter_id()))
                .map(result -> String.format(
                        "center id - %d, %s, %s, %s, %d has %d slots available, book fast!",
                        result.getCenter_id(),
                        result.getName(),
                        result.getAddress(),
                        result.getBlock_name(),
                        result.getPincode(),
                        result.getSessions().get(0).getAvailable_capacity()
                        )
                )
                .collect(Collectors.joining("\n\n"));
    }
}
