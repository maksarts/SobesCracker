package ru.maksarts.sobescracker.service.tgcommands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.constants.TgFormat;
import ru.maksarts.sobescracker.dto.telegram.ReplyParameters;
import ru.maksarts.sobescracker.dto.telegram.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.model.Course;
import ru.maksarts.sobescracker.repository.CourseRepository;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;

import java.util.List;
import java.util.Optional;

@Service
public class CommandsMainBotTgCommandHandler implements MainBotTgCommandHandler {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public String getCommand() {
        return COURSES;
    }

    @Override
    public Optional<SendMessage> handle(Update update) {
        List<Course> courses = courseRepository.findAll();

        ReplyParameters replyParameters = ReplyParameters.builder()
                .allow_sending_without_reply(true)
                .message_id(update.getMessage().getMessage_id())
                .build();

        SendMessage msg = SendMessage.builder()
                .text(buildAnswer(courses))
                .chat_id(update.getMessage().getChat().getId().toString())
                .reply_parameters(replyParameters)
                .parse_mode(TgFormat.PARSE_MODE_HTML.getTag())
                .build();

        //TODO кнопки для выбора действий с курсами НАЧАТЬ ПОДГОТОВКУ | ПОДПИСАТЬСЯ НА РАССЫЛКУ

        return Optional.ofNullable(msg);
    }

    private String buildAnswer(List<Course> courses){
        StringBuilder builder = new StringBuilder();
        courses.forEach(course -> {
            builder.append("<b>").append(course.getName()).append(":").append("</b>")
                    .append("\n")
                    .append(course.getDescription())
                    .append("\n\n");
        });
        return builder.toString();
    }
}
