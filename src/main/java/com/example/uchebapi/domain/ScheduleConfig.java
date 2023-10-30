package com.example.uchebapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
public class ScheduleConfig implements HavingGroupId {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDate firstDay = LocalDate.now().withDayOfMonth(1);

    @Range(message = "Start time must be in range (0, 1440)", min = 0, max = 1440)
    @Column(nullable = false)
    private Integer startTime = 540;

    @Range(message = "Lesson duration must be in range (0,360)", min = 0, max = 360)
    @Column(nullable = false)
    private Integer lessonDuration = 90;

    @Range(message = "Week cycle must be in range (1,5)", min = 1, max = 5)
    @Column(nullable = false)
    private Integer weeksCycle = 2;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Integer> breaksSchedule = Arrays.asList(10, 30, 10, 30, 10, 30, 10, 30);

    @JsonIgnore
    @JoinColumn(name = "groupId", insertable = false, updatable = false, nullable = false)
    @OneToOne() //TODO: FIX DELETE
    private LearningGroup group;
    @JsonIgnore
    private UUID groupId;
}
