package ru.maksarts.sobescracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import ru.maksarts.sobescracker.dto.telegram.Update;

@Controller
@Slf4j
public class LogBotController extends LongPoolingTelegramBot {
    @Autowired
    public LogBotController(@Value("${telegram-api.token.log}") String token,
                              RestTemplate restTemplate) {
        super(token, 0, restTemplate);
    }


    @Override
    protected void handle(Update update) {
        log.info("[LOGGER] Update handled: message=[{}]", update.getMessage());
    }
}
