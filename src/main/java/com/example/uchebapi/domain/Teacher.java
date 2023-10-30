package com.example.uchebapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Builder
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
public class Teacher implements HavingGroupId {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(hidden = true)
    private UUID id;
    @Pattern(regexp = "[a-zA-Zа-яёЁА-Я0-9- ()]*")
    @NotBlank
    @Length(message = "First name should not exceed 30 symbols", max = 30)
    @Column(nullable = false)
    private String firstName;
    @Pattern(regexp = "[a-zA-Zа-яёЁА-Я0-9- ()]*")
    @NotBlank
    @Length(message = "First name should not exceed 30 symbols", max = 30)
    @Column(nullable = false)
    private String lastName;
    @Pattern(regexp = "[a-zA-Zа-яёЁА-Я0-9- ()]*")
    @Length(message = "First name should not exceed 30 symbols", max = 30)
    private String patronymic;
    @Column(columnDefinition = "text")
    @Length(message = "First name should not exceed 30 symbols", max = 700)
    private String info;

    @JsonIgnore
    @JoinColumn(name = "groupId",insertable = false, updatable = false, nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private LearningGroup group;
    private UUID groupId;

    @JsonIgnore
    public String getFullName() {
        return lastName + " " + firstName + " " + patronymic;
    }
}
