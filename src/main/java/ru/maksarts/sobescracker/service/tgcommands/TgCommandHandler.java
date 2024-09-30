package ru.maksarts.sobescracker.service.tgcommands;

import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.dto.telegram.updatehandleresult.UpdateHandlerResult;

import java.util.List;

public interface TgCommandHandler {
    String getCommand();
    List<UpdateHandlerResult> handle(Update update);

    String START = "/start";
    String COURSES = "/courses";
    String SUBSCRIPTIONS = "/subscriptions";
    String SUBSCRIBE = "/subscribe";
    String UNSUBSCRIBE = "/unsubscribe";
    String STUDY = "/study";
}
