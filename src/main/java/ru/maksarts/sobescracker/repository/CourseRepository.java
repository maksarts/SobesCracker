package ru.maksarts.sobescracker.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.maksarts.sobescracker.model.Course;

import java.util.Optional;
import java.util.UUID;

//TODO ограничить создание сущностей Course через этот интерфейс
public interface CourseRepository extends JpaRepository<Course, UUID> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
                attributePaths = "types")
    Optional<Course> getByName(String name);
}
