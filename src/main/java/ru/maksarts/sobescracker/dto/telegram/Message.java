
package ru.maksarts.sobescracker.dto.telegram;

import lombok.Data;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.InlineKeyboardMarkup;

import java.io.Serializable;
import java.time.Instant;

@Data
public class Message implements Serializable {
    private Chat chat;
    private Instant date;
    private From from;
    private Integer message_id;
    private String text;
    private Boolean isCommand;
    private InlineKeyboardMarkup reply_markup;
}
