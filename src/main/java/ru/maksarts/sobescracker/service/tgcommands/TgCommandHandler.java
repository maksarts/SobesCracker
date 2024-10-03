package ru.maksarts.sobescracker.service.tgcommands;

import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.dto.telegram.tgmethod.TgMethod;

import java.util.List;

public interface TgCommandHandler {
    String getCommand();
    List<TgMethod> handle(Update update);
}
