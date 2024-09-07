package ru.maksarts.sobescracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maksarts.sobescracker.model.Settings;
import ru.maksarts.sobescracker.model.Subscriber;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SettingsRepository extends JpaRepository<Settings, UUID> {
    Optional<Settings> getSettingsByUserId(Subscriber userId);
}
