package com.example.uchebapi.dtos;

import lombok.Builder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
public record TaskDto(UUID id, UUID subjectId, String subjectName, String title, String content, Date deadline, List<Attachment> attachments,
                      Boolean isDone) {
}
