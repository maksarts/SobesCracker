package ru.maksarts.sobescracker.service.tgcommands.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.constants.TgParseMode;
import ru.maksarts.sobescracker.dto.telegram.Message;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.dto.telegram.tgmethod.EditMessageText;
import ru.maksarts.sobescracker.dto.telegram.tgmethod.TgMethod;
import ru.maksarts.sobescracker.model.Question;
import ru.maksarts.sobescracker.repository.QuestionRepository;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnswerMainBotTgCommandHandler implements MainBotTgCommandHandler {

    private final MessageSource messages;

    private final QuestionRepository questionRepository;

    @Override
    public String getCommand() {
        return ANSWER;
    }

    @Override
    public List<TgMethod> handle(Update update) {
        if(update.getCallback_query() != null){
            String[] data = update.getCallback_query().getData().trim().split(" ");
            if(data.length > 1){
                try {

                    UUID questionId = UUID.fromString(data[1]);
                    Message messageToEdit = update.getCallback_query().getMessage();
                    Integer chatId = update.getChatIdFrom();

                    Question question = questionRepository.findById(questionId).orElseThrow(EntityNotFoundException::new);

                    String newText = buildMessageText(question);

                    EditMessageText editMessageText = EditMessageText.builder()
                            .text(newText)
                            .chat_id(chatId)
                            .message_id(messageToEdit.getMessage_id())
                            .parse_mode(TgParseMode.PARSE_MODE_HTML.getTag())
                            .reply_markup(messageToEdit.getReply_markup())
                            .build();

                    return List.of(editMessageText);

                } catch (IllegalArgumentException ex){
                    log.error("Cannot resolve questionId from string [{}]", data[1]);
                }
            }
        }
        return null;
    }

    private String buildMessageText(Question question) {
        StringBuilder builder = new StringBuilder();
        builder.append("<b>")
                .append(messages.getMessage("study.title", null, Locale.forLanguageTag("ru-RU")))
                .append("</b>")
                .append("\n")
                .append(question.getContent())
                .append("\n\n")
                .append("<b>")
                .append(messages.getMessage("study.answer", null, Locale.forLanguageTag("ru-RU")))
                .append("</b>")
                .append("\n")
                .append(question.getAnswer());
        return builder.toString();
    }
}
