package ru.maksarts.sobescracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import ru.maksarts.sobescracker.dto.telegram.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.service.MainBotService;

import java.util.Optional;

@Controller
@Slf4j
public class MainBotController extends LongPoolingTelegramBot {

    @Autowired
    LogBotController botLogger;
    @Autowired
    MainBotService mainBotService;

    @Autowired
    public MainBotController(@Value("${telegram-api.token.main}") String token,
                              RestTemplate restTemplate) {
        super(token, 100, restTemplate);
    }

    private void sendAnswer(Object answer, Long chatId){ // kafka listener
        // send answer
    }

    private void sendOnSubscription(Object answer, Long chatId){ // kafka listener
        // send answer
    }


    @Override
    protected void handle(Update update) {
        log.info("[MAIN] Update to handle: message=[{}]", update.getMessage());
        Optional<SendMessage> answer = mainBotService.handleUpdate(update);
        answer.ifPresent(this::sendMessage);
    }
}
