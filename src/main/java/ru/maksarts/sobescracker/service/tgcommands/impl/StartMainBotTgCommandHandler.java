package ru.maksarts.sobescracker.service.tgcommands.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.network.Send;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.dto.telegram.From;
import ru.maksarts.sobescracker.dto.telegram.Message;
import ru.maksarts.sobescracker.dto.telegram.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.model.TgUser;
import ru.maksarts.sobescracker.repository.TgUserRepository;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;

import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class StartMainBotTgCommandHandler implements MainBotTgCommandHandler {

    @Autowired
    MessageSource messages;
    @Autowired
    TgUserRepository tgUserRepository;

    @Override
    public String getCommand() {
        return START;
    }

    @Override
    public Optional<SendMessage> handle(Update update) {
        log.info("command {} invoked", START);
        Message message = update.getMessage();
        From from = message.getFrom();

        TgUser user = TgUser.builder()
                .chatId(message.getChat().getId())
                .nickname(from.getUsername())
                .name(String.format("%s %s",from.getFirst_name(), from.getLast_name()))
                .build();

        if (!tgUserRepository.existsByChatId(user.getChatId())){
            tgUserRepository.save(user);
            log.info("New user registered={}", String.format("[%s][@%s]", user.getName(), user.getNickname()));
        }

        SendMessage answer = SendMessage.builder()
                .text(buildAnswer(update))
                .chat_id(update.getMessage().getChat().getId().toString())
                .build();

        return Optional.ofNullable(answer);
    }

    private String buildAnswer(Update update){
        String name = update.getMessage().getFrom().getFirst_name();
        String hello = String.format(
                messages.getMessage("start.hello", null, Locale.forLanguageTag("ru-RU")),
                name);
        String commands = messages.getMessage("commands.list", null, Locale.forLanguageTag("ru-RU"));
        return hello + "\n\n" + commands;
    }
}
