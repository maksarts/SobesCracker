package ru.maksarts.sobescracker.service.tgcommands;

import org.springframework.beans.factory.annotation.Autowired;
import ru.maksarts.sobescracker.service.MainBotService;

public interface MainBotTgCommandHandler extends TgCommandHandler{
    @Autowired
    default void register(MainBotService service){
        service.registerHandler(getCommand(), this);
    }

    String START = "/start";
    String COURSES = "/courses";
    String SUBSCRIPTIONS = "/subscriptions";
    String SUBSCRIBE = "/subscribe";
    String UNSUBSCRIBE = "/unsubscribe";
    String STUDY = "/study";
}
