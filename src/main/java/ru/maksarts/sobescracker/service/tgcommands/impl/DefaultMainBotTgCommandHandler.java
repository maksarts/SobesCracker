package ru.maksarts.sobescracker.service.tgcommands.impl;

import lombok.extern.slf4j.Slf4j;
import ru.maksarts.sobescracker.dto.telegram.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;

import java.util.Optional;

/**
 * Not a Bean!
 * Manual initialisation mandatory
 */
@Slf4j
public class DefaultMainBotTgCommandHandler implements MainBotTgCommandHandler {
    @Override
    public String getCommand() {
        return "default";
    }

    @Override
    public Optional<SendMessage> handle(Update update) {
        //TODO
        log.info("Unknown command [{}]", update.getMessage().getText().toLowerCase().trim());
        return Optional.empty();
    }
}
