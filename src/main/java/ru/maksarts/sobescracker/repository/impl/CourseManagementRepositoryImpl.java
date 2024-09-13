package ru.maksarts.sobescracker.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.maksarts.sobescracker.model.Course;
import ru.maksarts.sobescracker.model.CourseConditions;
import ru.maksarts.sobescracker.model.Question;
import ru.maksarts.sobescracker.model.Type;
import ru.maksarts.sobescracker.repository.CourseManagementRepository;
import ru.maksarts.sobescracker.repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class CourseManagementRepositoryImpl implements CourseManagementRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<Question> getQuestionsByCourse(Course course) {
        Query query = entityManager.createNativeQuery("SELECT * FROM ?1", Question.class);
        query.setParameter(1, course.getName());
        try {
            List<Question> questions = query.getResultList();
            return questions;
        } catch (SerializationException ex){
            log.error("Cannot serialize: {}", ex.getMessage(), ex);
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public Integer createCourse(String name, CourseConditions conditions) {
        Course course = Course.builder()
                .name(name)
                .minGrade(conditions.getMinGrade())
                .maxGrade(conditions.getMaxGrade())
                .types(conditions.getTypes())
                .build();

        courseRepository.save(course);

        Query query = entityManager.createNativeQuery(
                "create materialized view if not exists " + name +" as" +
                    " select * from question" +
                    " where" +
                    " type in (" + buildTypeCondition(conditions.getTypes()) + ")" +
                    " and" +
                    " grade <= " + (conditions.getMaxGrade() != null ? conditions.getMaxGrade().getVal() : 1000) +
                    " and" +
                    " grade >= " + (conditions.getMinGrade() != null ? conditions.getMinGrade().getVal() : 0)
        );

        query.executeUpdate();

        Query indexQuery = entityManager.createNativeQuery("create unique index on " + name + " (id)");

        indexQuery.executeUpdate();

        return 0;
    }

    @Override
    public Optional<Course> getCourseByName(String name) {
        return courseRepository.getByName(name);
    }

    private String buildTypeCondition(Set<Type> types){
        return types.stream()
                .map(type -> "'" + type.getId() + "'")
                .collect(Collectors.joining(","));
    }
}
