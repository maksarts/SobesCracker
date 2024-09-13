package ru.maksarts.sobescracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maksarts.sobescracker.model.Type;

import java.util.Optional;
import java.util.UUID;

public interface TypeRepository extends JpaRepository<Type, UUID> {
    Optional<Type> getByNameIgnoreCase(String name);
}
