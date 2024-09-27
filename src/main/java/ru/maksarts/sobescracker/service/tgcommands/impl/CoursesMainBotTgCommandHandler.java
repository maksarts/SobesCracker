package ru.maksarts.sobescracker.service.tgcommands.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        int nextPage = 0;
        if(update.getCallback_query() != null){
            String[] data = update.getCallback_query().getData().split(" ");
            if(data.length > 1) nextPage = Integer.parseInt(data[1]);
        }

        Long chatId = update.getChatIdFrom();

        Page<Course> courses = courseRepository.findAll(
                PageRequest.of(nextPage, 1, Sort.by("name").ascending()));

        if(courses.stream().findFirst().isPresent()) {
            Course course = courses.stream().findFirst().get();

            String text = buildAnswer(List.of(course));
            ReplyMarkup replyMarkup = buildReplyMarkup(nextPage, course);
            SendMessage msg = SendMessage.builder()
                    .text(text)
                    .chat_id(chatId.toString())
                    .parse_mode(TgFormat.PARSE_MODE_HTML.getTag())
                    .reply_markup(replyMarkup)
                    .build();

            return Optional.ofNullable(msg);
        }
        return Optional.empty();
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

    private ReplyMarkup buildReplyMarkup(int nextPage, Course course){
        InlineKeyboardButton subscribeButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("course.subscribe", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(SUBSCRIBE + " " + course.getId().toString())
                .build();

        InlineKeyboardButton studyButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("course.startStudy", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(STUDY + " " + course.getId().toString())
                .build();

        InlineKeyboardButton prevButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("move.previous", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(COURSES + " " + (Math.max(nextPage - 1, 0)))
                .build();

        InlineKeyboardButton nextButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("move.next", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(COURSES + " " + (nextPage + 1))
                .build();

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.addRow(List.of(subscribeButton, studyButton));
        keyboardMarkup.addRow(List.of(prevButton, nextButton));
        return keyboardMarkup;
    }
}
