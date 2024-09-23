package ru.maksarts.sobescracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import ru.maksarts.sobescracker.constants.TgLogLevel;
import ru.maksarts.sobescracker.dto.telegram.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.service.MainBotService;

import java.util.Optional;

@Controller
@Slf4j
public class MainBotController extends LongPoolingTelegramBot {

    private final LogBotController botLogger;
    private final MainBotService mainBotService;

    public MainBotController(@Value("${telegram-api.token.main}") String token,
                             RestTemplate restTemplate,
                             LogBotController botLogger,
                             MainBotService mainBotService) {
        super(token, 100, restTemplate);
        this.botLogger = botLogger;
        this.mainBotService = mainBotService;
    }

    private void sendAnswer(Object answer, Long chatId){ // kafka listener
        // send answer
    }

    private void sendOnSubscription(Object answer, Long chatId){ // kafka listener
        // send answer
    }


    @Override
    protected void handle(Update update) {
        log.info("[MAIN] Update to handle=[{}]", update.toString());
        try {

            Optional<SendMessage> answer = mainBotService.handleUpdate(update);
            answer.ifPresent(this::sendMessage);

        } catch (Exception ex){
            log.error("Cannot handle update: id={}: {}", update.getUpdate_id(), ex.getMessage(), ex);
            botLogger.log(String.format("Cannot handle update: id=%s", update.getUpdate_id()),
                    TgLogLevel.ERROR,
                    ex);
        }
    }

}
