
package ru.maksarts.sobescracker.dto.telegram;

import lombok.Data;

import java.io.Serializable;

@Data
public class Chat implements Serializable {
    private String first_name;
    private Integer id;
    private String last_name;
    private String type;
    private String username;
}
