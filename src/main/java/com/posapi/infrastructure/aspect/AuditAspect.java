package com.posapi.infrastructure.aspect;

import com.posapi.domain.model.AuditLog;
import com.posapi.application.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Aspect
@Component("infrastructureAuditAspect")
public class AuditAspect {
    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);
    private static final int MAX_PAYLOAD_LENGTH = 65535; // Límite estándar para columnas TEXT
    private static final String MASK_PATTERN = "\"(password|token|secret|credit_card|cvv)\"\\s*:\\s*\"([^\"]+)\"";

    private final AuditLogService auditLogService;

    public AuditAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @AfterReturning(pointcut = "@annotation(loggableAction)", returning = "result")
    public void logAction(JoinPoint joinPoint, @NonNull LoggableAction loggableAction, Object result) {
        saveLog(joinPoint, loggableAction, "SUCCESS", null);
    }

    @AfterThrowing(pointcut = "@annotation(loggableAction)", throwing = "ex")
    public void logFailure(JoinPoint joinPoint, @NonNull LoggableAction loggableAction, @NonNull Exception ex) {
        saveLog(joinPoint, loggableAction, "ERROR", ex.getMessage());
    }

    private void saveLog(JoinPoint joinPoint, LoggableAction loggableAction, String status, String error) {
        try {
            String user = getCurrentUser();
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            String ip = "UNKNOWN";
            String payload = null;

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ip = getClientIp(request);
                payload = getPayload(request);
            }

            AuditLog log = new AuditLog();
            log.setUsername(user);
            log.setAction(loggableAction != null ? loggableAction.value() : "UNKNOWN_ACTION");
            log.setMethod(joinPoint.getSignature().toShortString());
            log.setStatus(status);
            log.setErrorMessage(error);
            log.setIpAddress(ip);
            log.setPayload(truncatePayload(payload));
            log.setTimestamp(LocalDateTime.now());

            auditLogService.saveAuditLog(log);
        } catch (Exception e) {
            logger.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "ANONYMOUS";
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "UNKNOWN";
    }

    private String getPayload(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String raw = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
                return maskSensitiveData(raw);
            }
        }
        return null;
    }

    private String maskSensitiveData(String payload) {
        if (payload == null) return null;
        return payload.replaceAll(MASK_PATTERN, "\"$1\":\"****\"");
    }

    private String truncatePayload(String payload) {
        if (payload != null && payload.length() > MAX_PAYLOAD_LENGTH) {
            return payload.substring(0, MAX_PAYLOAD_LENGTH - 3) + "...";
        }
        return payload;
    }
}