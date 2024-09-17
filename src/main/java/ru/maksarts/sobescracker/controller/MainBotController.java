package ru.maksarts.sobescracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import ru.maksarts.sobescracker.dto.telegram.Update;

@Controller
@Slf4j
public class MainBotController extends LongPoolingTelegramBot {
    @Autowired
    public MainBotController(@Value("${telegram-api.token.main}") String token,
                              RestTemplate restTemplate) {
        super(token, 100, restTemplate);
    }


    @Override
    protected void handle(Update update) {
        log.info("[MAIN] Update handled: message=[{}]", update.getMessage());
    }
}
