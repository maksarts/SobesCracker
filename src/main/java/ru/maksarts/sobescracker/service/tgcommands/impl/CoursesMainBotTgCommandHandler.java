package ru.maksarts.sobescracker.service.tgcommands.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.constants.TgFormat;
import ru.maksarts.sobescracker.dto.telegram.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.InlineKeyboardButton;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.InlineKeyboardMarkup;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.ReplyMarkup;
import ru.maksarts.sobescracker.model.Course;
import ru.maksarts.sobescracker.repository.CourseRepository;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoursesMainBotTgCommandHandler implements MainBotTgCommandHandler {

    private final MessageSource messages;
    private final CourseRepository courseRepository;

    @Override
    public String getCommand() {
        return COURSES;
    }

    @Override
    public Optional<SendMessage> handle(Update update) {
        List<Course> courses = courseRepository.findAll();

        Long chatId = update.getChatIdFrom();

        SendMessage msg = SendMessage.builder()
                .text(buildAnswer(courses))
                .chat_id(chatId.toString())
                .parse_mode(TgFormat.PARSE_MODE_HTML.getTag())
                .reply_markup(buildReplyMarkup())
                .build();

        return Optional.ofNullable(msg);
    }

    private String buildAnswer(List<Course> courses){
        StringBuilder builder = new StringBuilder();
        courses.forEach(course -> builder
                .append("<b>").append(course.getName()).append(":").append("</b>")
                .append("\n")
                .append(course.getDescription())
                .append("\n\n"));
        return builder.toString();
    }

    private ReplyMarkup buildReplyMarkup(){
        InlineKeyboardButton subscribeButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("courses.subscribe", null, Locale.forLanguageTag("ru-RU")))
                .callback_data("/todo") //TODO
                .build();

        InlineKeyboardButton studyButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("courses.startStudy", null, Locale.forLanguageTag("ru-RU")))
                .callback_data("/todo") //TODO
                .build();

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.addRow(List.of(subscribeButton));
        keyboardMarkup.addRow(List.of(studyButton));
        return keyboardMarkup;
    }
}
