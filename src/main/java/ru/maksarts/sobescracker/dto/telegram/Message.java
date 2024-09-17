
package ru.maksarts.sobescracker.dto.telegram;

import lombok.Data;

import java.io.Serializable;

@Data
public class Message implements Serializable {
    private Chat chat;
    private Long date;
    private From from;
    private Long message_id;
    private String text;
    private Boolean isCommand;
}
