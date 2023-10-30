package com.example.uchebapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
public class Schedule implements HavingGroupId {
    @Schema(hidden = true)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer scheduleVersion;
    @JsonIgnore
    @JoinColumn(name = "groupId", insertable = false, updatable = false, nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private LearningGroup group;
    @Column(nullable = false)
    private UUID groupId;
}
