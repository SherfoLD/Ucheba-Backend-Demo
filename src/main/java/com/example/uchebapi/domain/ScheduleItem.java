package com.example.uchebapi.domain;

import com.example.uchebapi.enums.LessonTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"groupId", "lessonOrder", "weekDay", "week"})
        }
)

public class ScheduleItem implements HavingGroupId {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(hidden = true)
    private UUID id;

    @JsonIgnore
    @JoinColumn(name = "subjectId", insertable = false, updatable = false, nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Subject subject; //TODO: Check if teacher in same group

    @Column(nullable = false)
    private UUID subjectId;

    @JsonIgnore
    @JoinColumn(name = "teacherId", insertable = false, updatable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Teacher teacher; //TODO: Check if teacher in same group

    @JsonIgnore
    @JoinColumn(name = "groupId", insertable = false, updatable = false, nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private LearningGroup group;

    @Column(nullable = false)
    private UUID groupId;

    @Column()
    private UUID teacherId;

    @Column(nullable = false)
    private Integer lessonOrder;

    @Range(message = "Week day should be in range (0,6)", min = 0, max = 6)
    @Column(nullable = false)
    private Integer weekDay;

    @Column(nullable = true)
    private String room;

    @Column(nullable = false)
    @Enumerated
    private LessonTypes type;

    @Column(nullable = false)
    private Integer week;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Integer> excludedWeeks;
}
