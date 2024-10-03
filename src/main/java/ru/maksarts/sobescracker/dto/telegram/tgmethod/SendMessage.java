package ru.maksarts.sobescracker.dto.telegram.tgmethod;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;
import ru.maksarts.sobescracker.dto.telegram.ReplyParameters;
import ru.maksarts.sobescracker.dto.telegram.replymarkup.ReplyMarkup;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendMessage implements Serializable, TgMethod {
    @NonNull
    private String text;

    @NonNull
    private String chat_id;

    private String parse_mode;

    private ReplyParameters reply_parameters;

    private ReplyMarkup reply_markup;
}
