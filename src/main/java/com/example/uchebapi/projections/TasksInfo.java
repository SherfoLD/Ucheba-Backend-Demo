package com.example.uchebapi.projections;

import java.util.Date;
import java.util.UUID;

/**
 * Projection for {@link com.example.uchebapi.domain.Task}
 */
public interface TasksInfo {
    UUID getId();

    String getTitle();

    Date getDeadline();

    SubjectInfo getSubject();

    /**
     * Projection for {@link com.example.uchebapi.domain.Subject}
     */
    interface SubjectInfo {
        String getName();
    }
}