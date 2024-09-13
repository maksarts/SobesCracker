package ru.maksarts.sobescracker.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "course")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Transient
    private QuestionGrade minGrade;

    @Basic
    @Column(name = "min_grade")
    private Integer minGradeVal; // needed to correct representation ENUM values in DB

    @Transient
    private QuestionGrade maxGrade;

    @Basic
    @Column(name = "max_grade")
    private Integer maxGradeVal; // needed to correct representation ENUM values in DB


    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "course_type",
            joinColumns = { @JoinColumn(name = "course_id") },
            inverseJoinColumns = { @JoinColumn(name = "type_id") }
    )
    Set<Type> types = new HashSet<>();



    @PostLoad
    void fillTransient() {
        if (minGradeVal != null) {
            this.minGrade = QuestionGrade.of(minGradeVal);
        }
        if (maxGradeVal != null) {
            this.maxGrade = QuestionGrade.of(maxGradeVal);
        }
    }

    @PrePersist
    @PreUpdate
    void fillPersistent() {
        if (minGrade != null) {
            this.minGradeVal = minGrade.getVal();
        }
        if (maxGrade != null) {
            this.maxGradeVal = maxGrade.getVal();
        }
    }
}
