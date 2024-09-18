package ru.maksarts.sobescracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.maksarts.sobescracker.constants.TgRequestMethod;
import ru.maksarts.sobescracker.dto.telegram.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Extend this class to easy build a component for long pooling connection with Telegram bot.<p>
 * Override {@code handle()} function to receive the updates.<p>
 * Use {@code sendMessage()} function to send messages.
 */
@Slf4j
public abstract class LongPoolingTelegramBot {
    protected String token;
    protected String apiUrl;
    protected Long lastReceivedUpdate;
    protected Integer limit;

    protected RestTemplate restTemplate;
    protected String getUpdatesUrl;
    protected String sendMessageUrl;

    /**
     *
     * @param token secure token of your bot
     * @param limit batch size in pooling updates from Telegram API
     * @param restTemplate reference to RestTemplate component
     */
    public LongPoolingTelegramBot(@NonNull String token,
                                  @Nullable Integer limit,
                                  @NonNull RestTemplate restTemplate){
        this.token = token;
        this.limit = limit == null ? 100 : limit;
        this.apiUrl = String.format("https://api.telegram.org/bot%s", token);
        this.lastReceivedUpdate = (long) -2; // при перезагрузке начать принимать с последнего
        this.restTemplate = restTemplate;


        getUpdatesUrl = UriComponentsBuilder.fromHttpUrl(apiUrl + TgRequestMethod.GET_UPDATES)
                .queryParam("limit", this.limit)
                .queryParam("offset", "{offset}")
                .encode()
                .toUriString();

        sendMessageUrl = UriComponentsBuilder.fromHttpUrl(apiUrl + TgRequestMethod.SEND_MESSAGE)
                .toUriString();
    }


    protected abstract void handle(Update update);



    @Scheduled(fixedRate = 1)
    protected void getUpdates(){
        Map<String, Object> params = new HashMap<>();
        params.put("offset", lastReceivedUpdate + 1);

        ResponseEntity<TelegramResponse> response = restTemplate.getForEntity(
                getUpdatesUrl,
                TelegramResponse.class,
                params);

        if(response.getStatusCode().is2xxSuccessful()) {
            if (response.getBody() != null) {
                List<Update> updates = response.getBody().getResult();
                for (Update update : updates) {
                    log.trace("Incoming update: {}", update.toString());

                    ifCommand(update);

                    // handle
                    handle(update);

                    lastReceivedUpdate = update.getUpdate_id();
                }
            }
        }
        else{
            log.debug("Cannot get updates: ok={}, description={}",
                    response.getBody() == null ? "[body=null]" : response.getBody().getOk(),
                    response.getBody() == null ? "[body=null]" : response.getBody().getDescription());
        }
    }

    private void ifCommand(Update update) {
        if(update.getMessage() != null){
            Message message = update.getMessage();
            String text = message.getText();
            if(text.startsWith("/")){
                message.setIsCommand(true);
            } else{
                message.setIsCommand(false);
            }
        }
    }


    public void sendMessage(String text, String chatId, Boolean markdown){
        sendMessage(text, chatId, markdown, null);
    }
    public void sendMessage(String text, String chatId, Integer replyMessageId){
        sendMessage(text, chatId, true, replyMessageId);
    }
    public void sendMessage(String text, String chatId){
        sendMessage(text, chatId, true, null);
    }
    public ResponseEntity<?> sendMessage(String text, String chatId, boolean markdown, Integer replyMessageId){
        ReplyParameters replyParameters = null;
        if(replyMessageId != null){
            replyParameters = ReplyParameters.builder()
                    .message_id(replyMessageId)
                    .allow_sending_without_reply(true)
                    .build();
        }

        SendMessage msg = SendMessage.builder()
                .chat_id(chatId)
                .text(text)
                .parse_mode(markdown ? "MarkdownV2" : null)
                .reply_parameters(replyParameters)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SendMessage> request = new HttpEntity<>(msg, headers);
        return restTemplate.postForEntity(sendMessageUrl, request, String.class);
    }
}
