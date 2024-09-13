package ru.maksarts.sobescracker.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.maksarts.sobescracker.model.Question;
import ru.maksarts.sobescracker.model.QuestionGrade;
import ru.maksarts.sobescracker.model.Type;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> getQuestionByType(Type type);
    List<Question> getQuestionByGradeValue(Integer gradeValue);
    List<Question> getQuestionByGradeValueAndType(Integer gradeValue, Type type);
}
