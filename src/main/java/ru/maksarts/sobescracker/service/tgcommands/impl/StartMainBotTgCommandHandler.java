package ru.maksarts.sobescracker.service.tgcommands.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.dto.telegram.From;
import ru.maksarts.sobescracker.dto.telegram.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.InlineKeyboardButton;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.InlineKeyboardMarkup;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.ReplyMarkup;
import ru.maksarts.sobescracker.model.TgUser;
import ru.maksarts.sobescracker.repository.TgUserRepository;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;
import ru.maksarts.sobescracker.service.tgcommands.TgCommandHandler;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StartMainBotTgCommandHandler implements MainBotTgCommandHandler {

    private final MessageSource messages;
    private final TgUserRepository tgUserRepository;

    @Override
    public String getCommand() {
        return START;
    }

    @Override
    public Optional<SendMessage> handle(Update update) {
        From from = update.getMessage() != null ? update.getMessage().getFrom() : update.getCallback_query().getFrom();
        Long chatId = update.getChatIdFrom();

        TgUser user = TgUser.builder()
                .chatId(chatId)
                .nickname(from.getUsername())
                .name(String.format("%s %s",from.getFirst_name(), from.getLast_name()))
                .build();

        if (!tgUserRepository.existsByChatId(user.getChatId())){
            tgUserRepository.save(user);
            log.info("New user registered={}", String.format("[%s][@%s]", user.getName(), user.getNickname()));
        }

        SendMessage answer = SendMessage.builder()
                .text(buildHello(update))
                .reply_markup(buildReplyMarkup())
                .chat_id(update.getMessage().getChat().getId().toString())
                .build();

        return Optional.ofNullable(answer);
    }

    private String buildHello(Update update){
        String name = update.getMessage().getFrom().getFirst_name();
        String hello = String.format(
                messages.getMessage("start.hello", null, Locale.forLanguageTag("ru-RU")),
                name);
        return hello;
    }

    private ReplyMarkup buildReplyMarkup(){
        InlineKeyboardButton coursesButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("start.courses", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(TgCommandHandler.COURSES)
                .build();

        InlineKeyboardButton subscriptionsButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("start.subscriptions", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(TgCommandHandler.SUBSCRIPTIONS)
                .build();

        InlineKeyboardButton studyButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("start.study", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(TgCommandHandler.STUDY)
                .build();

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.addRow(List.of(coursesButton));
        keyboardMarkup.addRow(List.of(subscriptionsButton));
        keyboardMarkup.addRow(List.of(studyButton));
        return keyboardMarkup;
    }
}
