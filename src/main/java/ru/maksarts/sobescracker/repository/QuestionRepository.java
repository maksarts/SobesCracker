package ru.maksarts.sobescracker.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.maksarts.sobescracker.model.Question;
import ru.maksarts.sobescracker.model.QuestionGrade;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> getQuestionByType(String type, Pageable pageable);
    List<Question> getQuestionByGrade(QuestionGrade grade, Pageable pageable);
    List<Question> getQuestionByGradeAndType(QuestionGrade grade, String type, Pageable pageable);
}
