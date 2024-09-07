package ru.maksarts.sobescracker.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksarts.sobescracker.model.Subscriber;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {
    List<Subscriber> getSubscriberByType(String type, Pageable pageable);
    Optional<Subscriber> getSubscriberByChatId(String chatId);
}
