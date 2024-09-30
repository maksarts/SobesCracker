package ru.maksarts.sobescracker.service.tgcommands.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.dto.telegram.CallbackQuery;
import ru.maksarts.sobescracker.dto.telegram.From;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.dto.telegram.updatehandleresult.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.updatehandleresult.UpdateHandlerResult;
import ru.maksarts.sobescracker.model.Course;
import ru.maksarts.sobescracker.model.Subscription;
import ru.maksarts.sobescracker.model.TgUser;
import ru.maksarts.sobescracker.repository.CourseRepository;
import ru.maksarts.sobescracker.repository.SubscriptionRepository;
import ru.maksarts.sobescracker.repository.TgUserRepository;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnsubscribeMainBotTgCommandHandler implements MainBotTgCommandHandler {

    private final MessageSource messages;

    private final TgUserRepository tgUserRepository;
    private final CourseRepository courseRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public String getCommand() {
        return UNSUBSCRIBE;
    }

    @Override
    public List<UpdateHandlerResult> handle(Update update) {
        CallbackQuery callbackQuery = update.getCallback_query();
        Integer chatId = update.getChatIdFrom();

        if(callbackQuery != null){
            From from = callbackQuery.getFrom();
            TgUser tgUser = tgUserRepository.getTgUserByChatId(chatId)
                    .orElse(TgUser.builder()
                            .name(from.getFirst_name() + " " + from.getLast_name())
                            .chatId(chatId)
                            .nickname(from.getUsername())
                            .build());
            tgUser = tgUserRepository.saveAndFlush(tgUser);

            String[] data = callbackQuery.getData().trim().split(" ");
            if(data.length > 1){
                String courseId = data[1];
                Course course = courseRepository.findById(UUID.fromString(courseId)).orElse(null);
                if(course != null){

                    Subscription subscription = subscriptionRepository
                            .getSubscriptionByTgUserAndCourse(tgUser, course).orElse(null);

                    if(subscription != null){
                        subscriptionRepository.delete(subscription);
                    }

                    SendMessage msg = makeAnswerMessage(course, chatId.toString());
                    return List.of(msg);
                }
            }
        }
        return null;
    }

    private SendMessage makeAnswerMessage(Course course, String chatId){
        return SendMessage.builder()
                .text(String.format(
                        messages.getMessage("subscribe.unsubscribe.success", null, Locale.forLanguageTag("ru-RU")),
                        course.getName())
                )
                .chat_id(chatId)
                .build();
    }
}
