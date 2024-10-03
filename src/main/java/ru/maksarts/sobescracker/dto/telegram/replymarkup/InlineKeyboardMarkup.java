package ru.maksarts.sobescracker.dto.telegram.replymarkup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InlineKeyboardMarkup implements ReplyMarkup, Serializable {
    private List<List<InlineKeyboardButton>> inline_keyboard;

    public InlineKeyboardMarkup(){
        inline_keyboard = new ArrayList<>();
    }

    public void addRow(List<InlineKeyboardButton> row){
        inline_keyboard.add(row);
    }
}
