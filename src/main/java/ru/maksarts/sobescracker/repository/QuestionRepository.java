package ru.maksarts.sobescracker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.maksarts.sobescracker.model.Course;
import ru.maksarts.sobescracker.model.Question;
import ru.maksarts.sobescracker.model.Type;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> getQuestionByType(Type type);
    List<Question> getQuestionByGradeValue(Integer gradeValue);
    List<Question> getQuestionByGradeValueAndType(Integer gradeValue, Type type);

    @Query(value = "SELECT q FROM Question q " +
            "WHERE q.gradeValue <= :maxGrade " +
            "AND q.type IN (:types)" +
            "AND q NOT IN (:excluded)",

            countQuery = "SELECT count(q) FROM Question q " +
                    "WHERE q.gradeValue <= :maxGrade " +
                    "AND q.type IN (:types)" +
                    "AND q NOT IN (:excluded)"
            )
    Page<Question> getQuestionByCourseParamsAndExcluded(@Param("maxGrade") Integer grade,
                                                  @Param("types") List<Type> types,
                                                  @Param("excluded") List<Question> excluded,
                                                  Pageable pageable);


    @Query(value = "SELECT q FROM Question q " +
            "WHERE q.gradeValue <= :maxGrade " +
            "AND q.type IN (:types)",

            countQuery = "SELECT count(q) FROM Question q " +
                    "WHERE q.gradeValue <= :maxGrade " +
                    "AND q.type IN (:types)"
    )
    Page<Question> getQuestionByCourseParams(@Param("maxGrade") Integer grade,
                                             @Param("types") List<Type> types,
                                             Pageable pageable);


    @Query(value = "SELECT q FROM Question q " +
            "WHERE q.gradeValue <= :maxGrade " +
            "AND q.type IN (:types)"
    )
    List<Question> getQuestionsByCourseParams(@Param("maxGrade") Integer grade,
                                              @Param("types") List<Type> types);


    default Page<Question> getQuestionByCourseAndExcluded(Course course, Set<Question> excluded, Pageable pageable){
        if(excluded == null || excluded.isEmpty()){
            return getQuestionByCourseParams(
                    course.getMaxGradeVal(),
                    course.getTypes().stream().toList(),
                    pageable
            );
        } else {
            return getQuestionByCourseParamsAndExcluded(
                    course.getMaxGradeVal(),
                    course.getTypes().stream().toList(),
                    excluded.stream().toList(),
                    pageable
            );
        }
    }

    default List<Question> getQuestionsByCourse(Course course){
        return getQuestionsByCourseParams(
                    course.getMaxGradeVal(),
                    course.getTypes().stream().toList()
                );
    }
}
