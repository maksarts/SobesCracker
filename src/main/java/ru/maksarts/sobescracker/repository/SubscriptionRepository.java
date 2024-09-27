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
    List<Subscription> getSubscriptionByTgUser(TgUser tgUser);
    List<Subscription> getSubscriptionByCourse(Course course);

    Optional<Subscription> getSubscriptionByTgUserAndCourse(TgUser tgUser, Course course);
}
