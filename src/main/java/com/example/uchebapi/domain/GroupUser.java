package com.example.uchebapi.domain;

import com.example.uchebapi.enums.GroupRoles;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Builder
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
public class GroupUser implements HavingGroupId {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "group_id")
    private LearningGroup group;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private VkUser user;

    @Enumerated(EnumType.ORDINAL)
    private GroupRoles role = GroupRoles.Reader;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<UUID, Boolean> completedTasks = new HashMap<>();

    public void setCompleted(UUID taskId) {
        if (completedTasks == null)
            completedTasks = new HashMap<>();
        completedTasks.putIfAbsent(taskId, false);
        completedTasks.put(taskId, !completedTasks.get(taskId));
    }

    public boolean checkCompleted(UUID taskId) {
        if (completedTasks == null)
            completedTasks = new HashMap<>();
        if (completedTasks.get(taskId) == null)
            return false;
        return completedTasks.get(taskId);
    }

    @Override
    public UUID getGroupId() {
        return group.getId();
    }
}
