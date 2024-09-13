package ru.maksarts.sobescracker.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.maksarts.sobescracker.model.TgUser;

import java.util.Optional;
import java.util.UUID;

public interface TgUserRepository extends JpaRepository<TgUser, UUID> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = "excludedQuestions")
    Optional<TgUser> getTgUserByChatId(String chatId);
}
