package ru.maksarts.sobescracker.dto.telegram;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TelegramResponse implements Serializable {
    private String ok;
    private List<Update> result;
    private String description;
}
