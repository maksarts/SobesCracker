package ru.maksarts.sobescracker.dto.telegram;

import lombok.Data;

import java.io.Serializable;

@Data
public class CallbackQuery implements Serializable {
    private String id;
    private From from;
    private String chat_instance;
    private String data;
    private Message message;
    private Boolean isCommand;
}
