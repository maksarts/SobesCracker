package ru.maksarts.sobescracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "question")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "type", nullable = false)
    private String type;

    @Transient
    private QuestionGrade grade;
    @Basic(optional = false)
    @Column(name = "grade", nullable = false)
    private Integer gradeValue; // needed to correct representation ENUM values in DB

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @ManyToMany(mappedBy = "excludedQuestions", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Subscriber> subscribers = new HashSet<>();

    @PostLoad
    void fillTransient() {
        if (gradeValue != null) {
            this.grade = QuestionGrade.of(gradeValue);
        }
    }

    @PrePersist
    @PreUpdate
    void fillPersistent() {
        if (grade != null) {
            this.gradeValue = grade.getVal();
        }
    }
}
