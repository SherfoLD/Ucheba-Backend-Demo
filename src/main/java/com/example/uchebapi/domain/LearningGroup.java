package com.example.uchebapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
public class LearningGroup implements HavingGroupId {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public LearningGroup(String name) {
        this.name = name;
    }

    @Pattern(regexp = "[a-zA-Zа-яёЁА-Я0-9- ()]*")
    @Size(message = "Group name cannot be larger than 40 symbols", max = 40)
    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GroupUser> groupUsers;

    private Integer ownerId;

    public UUID getGroupId() {
        return id;
    }
}
