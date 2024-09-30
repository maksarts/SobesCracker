package ru.maksarts.sobescracker.dto.telegram.updatehandleresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class DeleteMessage implements Serializable, UpdateHandlerResult {
    @NonNull
    private Integer chat_id;

    @NonNull
    private Integer message_id;
}
