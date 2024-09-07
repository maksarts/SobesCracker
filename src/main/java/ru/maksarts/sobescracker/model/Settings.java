package ru.maksarts.sobescracker.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "settings")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Settings {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Subscriber userId;

    @Column(name = "setting", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Setting setting;
}
