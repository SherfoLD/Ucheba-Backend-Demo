package com.example.uchebapi.aspects;

import com.example.uchebapi.domain.HavingGroupId;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Component
@Aspect
@RequiredArgsConstructor
public class AccessControlAspect {

    private final AuthenticationService authenticationService;
    private final ApplicationContext context;

    @Around("@annotation(accessControl)")
    public Object doAccessCheck(ProceedingJoinPoint pjp, AccessControl accessControl) throws Throwable {
        Object argument = pjp.getArgs()[0];
        GroupRoles minimalRequiredRole = accessControl.minimalRequiredRole();
        CrudRepository<? extends HavingGroupId, UUID> repo = context.getBean(accessControl.fromRepo());

        //Проверка на Empty тоже здесь. Для того кто вызывает это не очевидно
        UUID groupId = getGroupIdByType(argument, repo);
        if (groupId == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, //BAD_REQUEST ??
                    "The requested resource no longer exists");

        Optional<Integer> currentUserPrivilegeLevel = Optional.ofNullable(
                authenticationService.getPrivilegeLevel(groupId)
        );
        if (currentUserPrivilegeLevel.isEmpty() ||
                currentUserPrivilegeLevel.get() > minimalRequiredRole.ordinal()) //GroupRoles descending (Reader > Owner)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have enough rights to perform this operation.");

        return pjp.proceed();
    }

    private UUID getGroupIdByType(Object uuidOrDomainOfSomeType, CrudRepository<? extends HavingGroupId, UUID> repo) {
        UUID groupId = null;

        if (uuidOrDomainOfSomeType instanceof UUID) {
            Optional<? extends HavingGroupId> domain = repo.findById((UUID) uuidOrDomainOfSomeType);

            if (domain.isPresent())
                groupId = domain.get().getGroupId();
        }

        if (uuidOrDomainOfSomeType instanceof HavingGroupId)
            groupId = ((HavingGroupId) uuidOrDomainOfSomeType).getGroupId();
        
        return groupId;
    }
}