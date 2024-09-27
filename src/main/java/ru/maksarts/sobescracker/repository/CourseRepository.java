package ru.maksarts.sobescracker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.maksarts.sobescracker.model.Course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> getByName(String name);

    @NonNull
    @Override
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
                attributePaths = "types")
    List<Course> findAll();

    @NonNull
    @Override
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = "types")
    Page<Course> findAll(@NonNull Pageable pageable);
}
