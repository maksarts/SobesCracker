package ru.maksarts.sobescracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseConditions {
    Set<Type> types;
    QuestionGrade minGrade;
    QuestionGrade maxGrade;
}
