
package ru.maksarts.sobescracker.dto.telegram;

import lombok.Data;

@Data
public class From {
    private Long id;
    private String first_name;
    private String last_name;
    private Boolean is_bot;
    private Boolean is_premium;
    private String language_code;
    private String username;
}
