package ru.maksarts.sobescracker.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.maksarts.sobescracker.model.Course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> getByName(String name);

    @Override
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
                attributePaths = "types")
    List<Course> findAll();
}
