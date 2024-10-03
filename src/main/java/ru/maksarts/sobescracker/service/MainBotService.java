package ru.maksarts.sobescracker.service;

import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.dto.telegram.*;
import ru.maksarts.sobescracker.dto.telegram.tgmethod.TgMethod;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;
import ru.maksarts.sobescracker.service.tgcommands.impl.DefaultMainBotTgCommandHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainBotService {
    private final DefaultMainBotTgCommandHandler defaultCommandHandler;
    private final Map<String, MainBotTgCommandHandler> commandHandlers;

    public MainBotService(){
        commandHandlers = new HashMap<>();
        defaultCommandHandler = new DefaultMainBotTgCommandHandler();
    }
    public void registerHandler(String command, MainBotTgCommandHandler handler){
        commandHandlers.put(command, handler);
    }


    /**
     * @param update incoming update
     * @return Optional<> object. If .isPresent() contains message to send as answer
     */
    public List<TgMethod> handleUpdate(Update update){
        if(update.getMessage() != null){
            return handleMessage(update.getMessage(), update);
        } else if (update.getCallback_query() != null) {
            return handleCallbackQuery(update.getCallback_query(), update);
        }
        return null;
    }


    private List<TgMethod> handleMessage(Message message, Update update){
        if(message.getIsCommand()) {
            return handleCommand(message.getText().toLowerCase().trim(), update);
        }
        return null;
    }

    private List<TgMethod> handleCallbackQuery(CallbackQuery callbackQuery, Update update){
        if(callbackQuery.getIsCommand()) {
            return handleCommand(callbackQuery.getData().toLowerCase().trim(), update);
        }
        return null;
    }


    private List<TgMethod> handleCommand(String command, Update update){
        String mainCommand = command.split(" ")[0];
        return commandHandlers.getOrDefault(mainCommand, defaultCommandHandler).handle(update);
    }

}
