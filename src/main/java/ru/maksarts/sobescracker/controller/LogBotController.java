package ru.maksarts.sobescracker.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import ru.maksarts.sobescracker.constants.TgFormat;
import ru.maksarts.sobescracker.constants.TgLogLevel;
import ru.maksarts.sobescracker.dto.telegram.Update;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@Controller
@Slf4j
public class LogBotController extends LongPoolingTelegramBot {

    @Value("${telegram-api.log-chat-id}")
    private String logChatId;

    private static final String okTemplate = "```\n[%s]: %s```";
    private static final String exceptionTemplate = "```\n[%s]: %s\nException: %s```";

    private TgLogLevel logLevel;
    private Map<String, TgLogLevel> changeLevel;

    @Autowired
    public LogBotController(@Value("${telegram-api.token.log}") String token,
                              RestTemplate restTemplate) {
        super(token, 0, restTemplate);
        logLevel = TgLogLevel.INFO;
    }


    @PostConstruct
    private void initCommands(){
        changeLevel = new HashMap<>();
        changeLevel.put("/set_trace", TgLogLevel.TRACE);
        changeLevel.put("/set_debug", TgLogLevel.DEBUG);
        changeLevel.put("/set_info", TgLogLevel.INFO);
        changeLevel.put("/set_warn", TgLogLevel.WARN);
        changeLevel.put("/set_error", TgLogLevel.ERROR);
    }


    @Override
    protected void handle(Update update) {
//        log.info("[LOGGER] Update handled: message=[{}]", update.getMessage());
        if(update.getMessage().getChat().getId().toString().equals(logChatId) && update.getMessage().getIsCommand()){
            handleCommand(update);
        }
    }

    public void log(String text, TgLogLevel level){
        log(text, level, null);
    }
    public void log(String text, TgLogLevel level, Throwable ex){
        if(level.compareTo(logLevel) < 0) return;

        if(ex == null) {
            sendMessage(
                    String.format(okTemplate, level.name(), text),
                    logChatId,
                    TgFormat.PARSE_MODE_MARKDOWN);
        } else{
            sendMessage(
                    String.format(exceptionTemplate, level.name(), text, ex.getMessage()),
                    logChatId,
                    TgFormat.PARSE_MODE_MARKDOWN);
        }
    }

    private void handleCommand(Update update) {
        String command = update.getMessage().getText().toLowerCase(Locale.ROOT).trim();
        if(changeLevel.containsKey(command)){
            logLevel = changeLevel.get(command);
            sendMessage(
                    String.format("Logging level changed to %s", logLevel.name()),
                    logChatId,
                    update.getMessage().getMessage_id());
        }
    }
}
