package ru.maksarts.sobescracker.dto.telegram.replymarkup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InlineKeyboardButton implements Serializable {
    @NonNull
    private String text;

    @Nullable
    private String url;

    @Nullable
    private String callback_data;
}
