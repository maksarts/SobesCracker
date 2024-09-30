
package ru.maksarts.sobescracker.dto.telegram;

import lombok.Data;

import java.io.Serializable;

@Data
public class Update implements Serializable {
    private Message message;
    private CallbackQuery callback_query;
    private Long update_id;
    private Integer chatIdFrom;
}
