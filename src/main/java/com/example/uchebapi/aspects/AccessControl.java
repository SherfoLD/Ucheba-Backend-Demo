package com.example.uchebapi.aspects;

import com.example.uchebapi.domain.HavingGroupId;
import com.example.uchebapi.enums.GroupRoles;
import org.springframework.data.repository.CrudRepository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AccessControl {

    Class<? extends CrudRepository<? extends HavingGroupId, UUID>> fromRepo();

    GroupRoles minimalRequiredRole();
}