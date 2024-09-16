
package ru.maksarts.sobescracker.dto.telegram;

import lombok.Data;

import java.io.Serializable;

@Data
public class Update implements Serializable {
    private Message message;
    private Long update_id;
}
