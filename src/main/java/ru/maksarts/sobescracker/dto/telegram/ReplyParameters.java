package ru.maksarts.sobescracker.dto.telegram;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReplyParameters implements Serializable {
    @NonNull
    private Integer message_id;
    private String chat_id;
    private Boolean allow_sending_without_reply;
}
