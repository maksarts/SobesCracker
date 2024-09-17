package ru.maksarts.sobescracker.service;

import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;
import ru.maksarts.sobescracker.service.tgcommands.impl.DefaultMainBotTgCommandHandler;

import java.util.HashMap;
import java.util.Map;

@Service
public class MainBotService {
    private final DefaultMainBotTgCommandHandler defaultCommandHandler;
    private final Map<String, MainBotTgCommandHandler> handlers;

    public MainBotService(){
        handlers = new HashMap<>();
        defaultCommandHandler = new DefaultMainBotTgCommandHandler();
    }
    public void registerHandler(String command, MainBotTgCommandHandler handler){
        handlers.put(command, handler);
    }




    public void handleUpdate(Update update){
        if(update.getMessage() != null && update.getMessage().getIsCommand()){
            handleCommand(update);
        }
    }


    private void handleCommand(Update update){
        String command = update.getMessage().getText().toLowerCase().trim();
        handlers.getOrDefault(command, defaultCommandHandler).handle(update);
    }

}
