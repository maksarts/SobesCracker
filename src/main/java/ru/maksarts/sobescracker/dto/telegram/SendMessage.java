package ru.maksarts.sobescracker.dto.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendMessage implements Serializable {
    @NonNull
    private String text;

    @NonNull
    private String chat_id;

    private String parse_mode;

    private ReplyParameters reply_parameters;

    //TODO entities - Array of MessageEntity
}
