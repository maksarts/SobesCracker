package ru.maksarts.sobescracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Setting implements Serializable {
    private QuestionGrade grade;
    private String type;
}
