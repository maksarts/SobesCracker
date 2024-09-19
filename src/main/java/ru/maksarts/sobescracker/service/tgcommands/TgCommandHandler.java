package ru.maksarts.sobescracker.service.tgcommands;

import ru.maksarts.sobescracker.dto.telegram.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.Update;

import java.util.Optional;

public interface TgCommandHandler {
    String getCommand();
    Optional<SendMessage> handle(Update update);

    String START = "/start";
    String COURSES = "/courses";
}
