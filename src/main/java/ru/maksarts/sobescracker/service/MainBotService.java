package ru.maksarts.sobescracker.service;

import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.dto.telegram.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;
import ru.maksarts.sobescracker.service.tgcommands.impl.DefaultMainBotTgCommandHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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




    public Optional<SendMessage> handleUpdate(Update update){
        if(update.getMessage() != null && update.getMessage().getIsCommand()){
            return handleCommand(update);
        }
        return Optional.empty();
    }


    private Optional<SendMessage> handleCommand(Update update){
        String command = update.getMessage().getText().toLowerCase().trim();
        return commandHandlers.getOrDefault(command, defaultCommandHandler).handle(update);
    }

}
