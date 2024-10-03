package ru.maksarts.sobescracker.service.tgcommands.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.maksarts.sobescracker.constants.TgParseMode;
import ru.maksarts.sobescracker.dto.telegram.From;
import ru.maksarts.sobescracker.dto.telegram.Update;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.InlineKeyboardButton;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.InlineKeyboardMarkup;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.ReplyMarkup;
import ru.maksarts.sobescracker.dto.telegram.tgmethod.DeleteMessage;
import ru.maksarts.sobescracker.dto.telegram.tgmethod.SendMessage;
import ru.maksarts.sobescracker.dto.telegram.tgmethod.TgMethod;
import ru.maksarts.sobescracker.model.*;
import ru.maksarts.sobescracker.repository.CourseRepository;
import ru.maksarts.sobescracker.repository.QuestionRepository;
import ru.maksarts.sobescracker.repository.SettingsRepository;
import ru.maksarts.sobescracker.repository.TgUserRepository;
import ru.maksarts.sobescracker.service.tgcommands.MainBotTgCommandHandler;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudyMainBotTgCommandHandler implements MainBotTgCommandHandler {

    private final MessageSource messages;

    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final SettingsRepository settingsRepository;
    private final TgUserRepository tgUserRepository;

    @Override
    public String getCommand() {
        return STUDY;
    }

    @Override
    public List<TgMethod> handle(Update update) {
        Integer chatId = update.getChatIdFrom();

        if(update.getMessage() != null){
            // only '/study' => continue study by settings
            return continueStudy(update, chatId);

        }
        if (update.getCallback_query() != null) {
            String[] data = update.getCallback_query().getData().trim().split(" ");

            if(data.length == 1){
                // only '/study' => continue study by settings
                return continueStudy(update, chatId);
            }
            else if(data.length == 2){
                // '/study <courseId>' => new study by specified course
                String courseId = data[1];
                return newStudy(chatId, courseId);
            }
            else if(data.length == 3){
                // '/study <courseId> <pageNum>' => retrieve next page by this course
                String courseId = data[1];
                Integer nextPage = Integer.parseInt(data[2]);
                Course course = courseRepository.findById(UUID.fromString(courseId)).orElse(null);
                if(course != null){
                    return nextQuestion(
                            chatId,
                            course,
                            nextPage,
                            update.getCallback_query().getMessage().getMessage_id());
                }
            }
        }

        return null;
    }

    private List<TgMethod> newStudy(Integer chatId, String courseId) {
        Course course = courseRepository.findById(UUID.fromString(courseId)).orElse(null);
        if(course != null){
            SendMessage startMessage = SendMessage.builder()
                    .chat_id(chatId.toString())
                    .text(String.format(
                            messages.getMessage("study.startmessage", null, Locale.forLanguageTag("ru-RU")),
                            course.getName()
                    ))
                    .build();

            List<TgMethod> startQuestion = nextQuestion(chatId, course, 0, null);

            if(startQuestion != null && !startQuestion.isEmpty()) {
                ArrayList<TgMethod> result = new ArrayList<>();
                result.add(startMessage);
                result.addAll(startQuestion);
                return result;
            }
        }
        return null;
    }

    private List<TgMethod> continueStudy(Update update, Integer chatId) {
        From from = null;
        if(update.getMessage() != null){
            from = update.getMessage().getFrom();
        } else if (update.getCallback_query() != null){
            from = update.getCallback_query().getFrom();
        }

        if(from != null) {
            TgUser tgUser = tgUserRepository.getTgUserByChatId(chatId)
                    .orElse(TgUser.builder()
                            .name(from.getFirst_name() + " " + from.getLast_name())
                            .chatId(chatId)
                            .nickname(from.getUsername())
                            .build());
            tgUser = tgUserRepository.saveAndFlush(tgUser);
            Settings settings = settingsRepository.getSettingsByUserId(tgUser).orElse(null);
            if(settings != null){

                // continue by settings
                Setting setting = settings.getSetting();
                UUID courseId = setting.getCurrentCourse();
                Integer lastPage = setting.getLastPage();
                Course course = courseRepository.findById(courseId).orElse(null);
                if(course != null){
                    return nextQuestion(chatId, course, lastPage, null);
                }

            } else {

                // settings are empty
                SendMessage msg = SendMessage.builder()
                        .chat_id(chatId.toString())
                        .text(messages.getMessage("study.emptysettings", null, Locale.forLanguageTag("ru-RU")))
                        .reply_markup(buildCoursesReplyMarkup())
                        .build();
                return List.of(msg);

            }

        }
        return null;
    }

    private List<TgMethod> nextQuestion(Integer chatId, Course course, Integer page, Integer messageIdToDelete) {
        TgUser tgUser = tgUserRepository.getTgUserByChatId(chatId).orElse(null);
        if(tgUser != null){

            Set<Question> excluded = tgUser.getExcludedQuestions();

            Page<Question> questions = questionRepository.getQuestionByCourseAndExcluded(
                    course,
                    excluded,
                    PageRequest.of(page, 1, Sort.by("id"))
            );

            Optional<Question> question = questions.get().findFirst();
            if(question.isPresent()){

                List<TgMethod> result = new ArrayList<>();

                if(messageIdToDelete != null) {
                    DeleteMessage dm = DeleteMessage.builder()
                            .chat_id(chatId)
                            .message_id(messageIdToDelete)
                            .build();
                    result.add(dm);
                }

                SendMessage msg = SendMessage.builder()
                        .chat_id(chatId.toString())
                        .text(buildQuestionMessage(question.get()))
                        .parse_mode(TgParseMode.PARSE_MODE_HTML.getTag())
                        .reply_markup(buildQuestionReplyMarkup(page, question.get(), course))
                        .build();

                result.add(msg);

                updateSettings(tgUser, page, course);

                return result;
            }
        }
        return null;
    }

    private void updateSettings(TgUser tgUser, Integer page, Course course) {
        Settings settings = settingsRepository.getSettingsByUserId(tgUser)
                .orElse(Settings.builder()
                        .userId(tgUser)
                        .setting(new Setting())
                        .build());
        settings.getSetting().setLastPage(page);
        settings.getSetting().setCurrentCourse(course.getId());
        settingsRepository.save(settings);
    }


    private String buildQuestionMessage(Question question){
        StringBuilder builder = new StringBuilder();
        builder.append("<b>")
                .append(messages.getMessage("study.title", null, Locale.forLanguageTag("ru-RU")))
                .append("</b>")
                .append("\n")
                .append(question.getContent());
        return builder.toString();
    }

    private ReplyMarkup buildCoursesReplyMarkup(){
        InlineKeyboardButton coursesButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("start.courses", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(COURSES)
                .build();

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.addRow(List.of(coursesButton));
        return keyboardMarkup;
    }

    private ReplyMarkup buildQuestionReplyMarkup(int page, Question question, Course course){
        InlineKeyboardButton answerButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("study.showanswer", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(ANSWER + " " + question.getId())
                .build();

        InlineKeyboardButton excludeButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("study.exclude", null, Locale.forLanguageTag("ru-RU")))
                .callback_data("/exclude" + " " + question.getId()) //TODO
                .build();

        InlineKeyboardButton bugButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("study.reportbug", null, Locale.forLanguageTag("ru-RU")))
                .callback_data("/bug" + " " + question.getId()) //TODO
                .build();

        InlineKeyboardButton prevButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("move.previous", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(STUDY + " " + course.getId() + " " + (Math.max(page - 1, 0)))
                .build();

        InlineKeyboardButton nextButton = InlineKeyboardButton.builder()
                .text(messages.getMessage("move.next", null, Locale.forLanguageTag("ru-RU")))
                .callback_data(STUDY + " " + course.getId() + " " + (page + 1))
                .build();

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.addRow(List.of(prevButton, nextButton));
        keyboardMarkup.addRow(List.of(answerButton));
        keyboardMarkup.addRow(List.of(bugButton)); //TODO может быть сделать не такими явными
        keyboardMarkup.addRow(List.of(excludeButton)); //TODO может быть сделать не такими явными
        return keyboardMarkup;
    }

}
