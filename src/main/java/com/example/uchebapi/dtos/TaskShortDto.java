package com.example.uchebapi.dtos;

import lombok.Builder;

import java.util.Date;
import java.util.UUID;

@Builder
public record TaskShortDto(UUID id, String title, String subject, Date deadline, boolean isDone) {
}
