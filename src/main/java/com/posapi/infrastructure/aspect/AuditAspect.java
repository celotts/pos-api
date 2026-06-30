package com.posapi.infrastructure.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posapi.domain.repository.UserRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.audit.AuditLogEntity;
import com.posapi.infrastructure.adapter.output.persistence.repository.audit.AuditLogJpaRepository;
import com.posapi.infrastructure.security.SecurityContextHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogJpaRepository auditLogRepository;
    private final SecurityContextHelper securityContextHelper;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(com.posapi.infrastructure.aspect.Auditable)")
    public void auditableMethods() {}

    @AfterReturning(pointcut = "auditableMethods()", returning = "result")
    public void auditMethod(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Auditable auditable = method.getAnnotation(Auditable.class);

            UUID userId = securityContextHelper.getCurrentUsername()
                    .flatMap(userRepository::findByEmail)
                    .map(com.posapi.domain.model.user.User::getId)
                    .orElse(null);

            UUID recordId = getRecordId(joinPoint, result);
            String newValue = convertToJson(result);

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            AuditLogEntity logEntry = AuditLogEntity.builder()
                    .tableName(auditable.tableName())
                    .recordId(recordId)
                    .action(auditable.action())
                    .newValue(newValue)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .userId(userId)
                    .build();

            auditLogRepository.save(logEntry);
            log.info("Audit log created for action: {} on table {}", auditable.action(), auditable.tableName());

        } catch (Exception e) {
            log.error("Error creating audit log", e);
        }
    }

    private UUID getRecordId(JoinPoint joinPoint, Object result) {
        // First, try to find a UUID in the method arguments
        return Arrays.stream(joinPoint.getArgs())
                .filter(UUID.class::isInstance)
                .map(UUID.class::cast)
                .findFirst()
                .orElseGet(() -> {
                    // If not in args, try to get it from the result (for create operations)
                    if (result instanceof Optional) {
                        Object unwrapped = ((Optional<?>) result).orElse(null);
                        return getFromObject(unwrapped);
                    }
                    return getFromObject(result);
                });
    }

    private UUID getFromObject(Object obj) {
        if (obj == null) return null;
        try {
            Method getIdMethod = obj.getClass().getMethod("getId");
            return (UUID) getIdMethod.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.warn("Could not convert object to JSON for audit log", e);
            return null;
        }
    }
}
