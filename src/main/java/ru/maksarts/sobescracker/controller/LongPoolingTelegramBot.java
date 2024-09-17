package ru.maksarts.sobescracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.maksarts.sobescracker.dto.telegram.TelegramResponse;
import ru.maksarts.sobescracker.dto.telegram.Update;

import java.util.List;

@Slf4j
public abstract class LongPoolingTelegramBot {
    protected String token;
    protected String apiUrl;
    protected Long lastReceivedUpdate;
    protected Integer limit;

    protected RestTemplate restTemplate;
    protected String getUpdatesUrl;

    public LongPoolingTelegramBot(@NonNull String token,
                                  @Nullable Integer limit,
                                  @NonNull RestTemplate restTemplate){
        this.token = token;
        this.limit = limit == null ? 100 : limit;
        this.apiUrl = String.format("https://api.telegram.org/bot%s", token);
        this.lastReceivedUpdate = (long) -1;
        this.restTemplate = restTemplate;

        getUpdatesUrl = UriComponentsBuilder.fromHttpUrl(apiUrl + TelegramRequestMethod.GET_UPDATES)
                .queryParam("limit", this.limit)
                .queryParam("offset", lastReceivedUpdate + 1)
                .toUriString();
    }

    @Scheduled(fixedRate = 1)
    protected void getUpdates(){

        String url = UriComponentsBuilder.fromHttpUrl(apiUrl + TelegramRequestMethod.GET_UPDATES)
                .queryParam("limit", this.limit)
                .queryParam("offset", lastReceivedUpdate + 1)
                .toUriString();

        ResponseEntity<TelegramResponse> response = restTemplate.getForEntity(url, TelegramResponse.class);
        if(response.getStatusCode().is2xxSuccessful()) {
            if (response.getBody() != null) {
                List<Update> updates = response.getBody().getResult();
                for (Update update : updates) {
                    log.trace("Incoming update: {}", update.toString());

                    // handle
                    handle(update);

                    lastReceivedUpdate = update.getUpdate_id();
                }
            }
        }
        else{
            log.debug("Cannot get updates: ok={}, description={}",
                    response.getBody() == null ? "[body=null]" : response.getBody().getOk(),
                    response.getBody() == null ? "[body=null]" : response.getBody().getDescription());
        }
    }

    protected abstract void handle(Update update);

}
