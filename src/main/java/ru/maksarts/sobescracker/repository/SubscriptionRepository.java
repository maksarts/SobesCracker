package ru.maksarts.sobescracker.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.maksarts.sobescracker.model.Course;
import ru.maksarts.sobescracker.model.Subscription;
import ru.maksarts.sobescracker.model.TgUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
                attributePaths = {"user_id", "course_id"})
    List<Subscription> getSubscriptionByTgUser(TgUser tgUser);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"user_id", "course_id"})
    List<Subscription> getSubscriptionByCourse(Course course);
}
