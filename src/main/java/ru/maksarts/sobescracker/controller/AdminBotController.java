package ru.maksarts.sobescracker.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.maksarts.sobescracker.dto.telegram.TelegramResponse;
import ru.maksarts.sobescracker.dto.telegram.Update;

import java.util.List;

@Controller
@Slf4j
public class AdminBotController {

    private static final String SEND_MESSAGE = "/sendMessage";
    private static final String GET_UPDATES = "/getUpdates";

    @Value("${telegram-api.token.admin}")
    private String token;
    private String apiUrl;

    private Long lastReceivedUpdate = (long) -1;

    @Autowired
    protected RestTemplate restTemplate;

    @PostConstruct
    public void init(){
        this.apiUrl = String.format("https://api.telegram.org/bot%s", token);
    }



    @Scheduled(fixedRate = 10)
    public void getUpdates(){

        String url = UriComponentsBuilder.fromHttpUrl(apiUrl + GET_UPDATES)
                .queryParam("limit", 100)
                .queryParam("offset", lastReceivedUpdate + 1)
                .toUriString();

        ResponseEntity<TelegramResponse> response = restTemplate.getForEntity(url, TelegramResponse.class);
        if(response.getStatusCode().is2xxSuccessful()) {
            if (response.getBody() != null) {
                List<Update> updates = response.getBody().getResult();
                for (Update update : updates) {
                    // handle
                    log.info("[ADMIN]: received updateId={}, from={}, text={}",
                            update.getUpdate_id(),
                            update.getMessage().getFrom().getUsername(),
                            update.getMessage().getText());
                    lastReceivedUpdate = update.getUpdate_id();
                }
            }
        }
        else{
            log.warn("Cannot get updates: ok={}, description={}",
                    response.getBody() == null ? "[body=null]" : response.getBody().getOk(),
                    response.getBody() == null ? "[body=null]" : response.getBody().getDescription());
        }

    }





}
