package ru.maksarts.sobescracker.service.tgcommands.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.constants.TgFormat;
import ru.maksarts.sobescracker.dto.telegram.From;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.InlineKeyboardButton;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.InlineKeyboardMarkup;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.ReplyMarkup;
import ru.maksarts.sobescracker.dto.telegram.updatehandleresult.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.updatehandleresult.UpdateHandlerResult;
import ru.maksarts.sobescracker.model.Subscription;
import ru.maksarts.sobescracker.model.TgUser;
import ru.maksarts.sobescracker.repository.SubscriptionRepository;
import ru.maksarts.sobescracker.repository.TgUserRepository;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;
import ru.maksarts.sobescracker.service.tgcommands.TgCommandHandler;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SubscriptionsMainBotTgCommandHandler implements MainBotTgCommandHandler {

    private final MessageSource messages;

    private final TgUserRepository tgUserRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public String getCommand() {
        return SUBSCRIPTIONS;
    }

    @Override
    public List<UpdateHandlerResult> handle(Update update) {
        Integer chatId = update.getChatIdFrom();
        From from = null;

        if(update.getCallback_query() != null){
            from = update.getCallback_query().getFrom();
        } else if (update.getMessage() != null) {
            from = update.getMessage().getFrom();
        }

        if(from != null) {
            TgUser tgUser = tgUserRepository.getTgUserByChatId(chatId)
                    .orElse(TgUser.builder()
                            .name(from.getFirst_name() + " " + from.getLast_name())
                            .chatId(chatId)
                            .nickname(from.getUsername())
                            .build());
            tgUser = tgUserRepository.saveAndFlush(tgUser);

            List<Subscription> subscriptions = subscriptionRepository.getSubscriptionByTgUser(tgUser);

            if(subscriptions.isEmpty()){

                SendMessage msg = SendMessage.builder()
                        .chat_id(chatId.toString())
                        .text(messages.getMessage("subscriptions.empty", null, Locale.forLanguageTag("ru-RU")))
                        .reply_markup(buildReplyMarkup())
                        .build();

                return List.of(msg);

            } else{
                SendMessage msg = SendMessage.builder()
                        .chat_id(chatId.toString())
                        .text(buildAnswer(subscriptions))
                        .parse_mode(TgFormat.PARSE_MODE_HTML.getTag())
                        .reply_markup(buildReplyMarkup())
                        .build();

                return List.of(msg);
            }
        }

        return null;
    }

    private ReplyMarkup buildReplyMarkup(){
        InlineKeyboardButton coursesButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("start.courses", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(COURSES)
                .build();

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.addRow(List.of(coursesButton));
        return keyboardMarkup;
    }

    private String buildAnswer(List<Subscription> subscriptions){
        StringBuilder builder = new StringBuilder();

        builder.append("<b>")
                .append(messages.getMessage("subscriptions.title", null, Locale.forLanguageTag("ru-RU")))
                .append("</b>")
                .append("\n\n");

        for(int i = 0; i < subscriptions.size(); i++){
            Subscription subscription = subscriptions.get(i);

            builder.append(i + 1).append(") ")
                    .append(subscription.getCourse().getName())
                    .append("\n");
        }

        return builder.toString();
    }
}
