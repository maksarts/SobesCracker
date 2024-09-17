package ru.maksarts.sobescracker.service.tgcommands;

import ru.maksarts.sobescracker.dto.telegram.Update;

public interface TgCommandHandler {
    String getCommand();
    void handle(Update update);

    String START = "/start";
}
