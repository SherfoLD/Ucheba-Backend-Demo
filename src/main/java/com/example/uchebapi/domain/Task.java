package com.example.uchebapi.domain;

import com.example.uchebapi.dtos.Attachment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
public class Task implements HavingGroupId {

    @Schema(hidden = true)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @JoinColumn(name = "groupId", insertable = false, updatable = false, nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private LearningGroup learningGroup;

    private UUID groupId;
    @JsonIgnore
    @JoinColumn(name = "subjectId", insertable = false, updatable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Subject subject;

    private UUID subjectId;

    @Size(message = "Title cannot exceed 100 symbols", max = 100)
    @NotBlank
    @Column(nullable = false)
    private String title;

    @Size(message = "Content cannot exceed 1500 symbols", max = 1500)
    @Lob
    private String content;

    @Column()
    private Date deadline;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Attachment> attachments;
}
