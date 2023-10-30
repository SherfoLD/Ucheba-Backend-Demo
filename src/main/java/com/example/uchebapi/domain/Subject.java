package com.example.uchebapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
public class Subject implements HavingGroupId {
    @Schema(hidden = true)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Pattern(regexp = "[a-zA-Zа-яёЁА-Я0-9- ()]*")
    @NotBlank
    @NotNull
    @Size(message = "Subject name cannot exceed 100 symbols", max = 100)
    private String name;

    @JsonIgnore
    @JoinColumn(name = "groupId",insertable = false, updatable = false, nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private LearningGroup group;

    private UUID groupId;
}
