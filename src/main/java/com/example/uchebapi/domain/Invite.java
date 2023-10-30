package com.example.uchebapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Builder
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
public class Invite implements HavingGroupId {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID groupId; //TODO: LINK TO OBJ

    private Integer vkId;

    private Integer validCount;

    private Date validUntil;
}
