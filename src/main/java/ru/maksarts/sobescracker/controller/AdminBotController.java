package ru.maksarts.sobescracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import ru.maksarts.sobescracker.dto.telegram.Update;

@Controller
@Slf4j
public class AdminBotController extends LongPoolingTelegramBot{

    @Value("${telegram-api.admin-chat-id}")
    private String adminChatId; //API_KEY

    @Autowired
    public AdminBotController(@Value("${telegram-api.token.admin}") String token,
                              RestTemplate restTemplate) {
        super(token, 100, restTemplate);
    }


    @Override
    protected void handle(Update update) {
        if(update.getMessage().getChat().getId().toString().equals(adminChatId)) {
            log.info("[ADMIN] Update handled: message=[{}]", update.getMessage());
        }
    }

    //TODO сделать рассылку всем пользователям мануально из админского бота
}
