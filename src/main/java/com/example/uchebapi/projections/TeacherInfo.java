package com.example.uchebapi.projections;

import java.util.UUID;

/**
 * Projection for {@link com.example.uchebapi.domain.Teacher}
 */
public interface TeacherInfo {
    UUID getId();

    String getFirstName();

    String getLastName();

    String getPatronymic();
}