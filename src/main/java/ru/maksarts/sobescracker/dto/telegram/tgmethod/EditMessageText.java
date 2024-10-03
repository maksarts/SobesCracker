package ru.maksarts.sobescracker.dto.telegram.tgmethod;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.ReplyMarkup;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditMessageText implements Serializable, TgMethod {
    @NonNull
    private String text;
    private Integer chat_id;
    private Integer message_id;
    private String parse_mode;
    private ReplyMarkup reply_markup;
}
