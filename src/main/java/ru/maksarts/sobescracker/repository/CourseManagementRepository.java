package ru.maksarts.sobescracker.repository;

import ru.maksarts.sobescracker.model.Course;
import ru.maksarts.sobescracker.model.CourseConditions;
import ru.maksarts.sobescracker.model.Question;

import java.util.List;
import java.util.Optional;

public interface CourseManagementRepository {
    List<Question> getQuestionsByCourse(Course course);

    Integer createCourse(String name, CourseConditions courseConditions);

    Optional<Course> getCourseByName(String name);
}
