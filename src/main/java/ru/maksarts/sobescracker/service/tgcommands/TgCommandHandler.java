package ru.maksarts.sobescracker.service.tgcommands;

import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.dto.telegram.updatehandleresult.UpdateHandlerResult;

import java.util.List;

public interface TgCommandHandler {
    String getCommand();
    List<UpdateHandlerResult> handle(Update update);
}
