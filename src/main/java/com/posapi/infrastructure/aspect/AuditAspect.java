package com.posapi.infrastructure.aspect;

import com.posapi.application.service.audit.AuditLogService;
import com.posapi.domain.model.AuditLog;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Aspect
@Component("infrastructureAuditAspect")
public class AuditAspect {
    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);
    private static final int MAX_PAYLOAD_LENGTH = 65535; // Límite estándar para columnas TEXT
    private static final Pattern MASK_PATTERN = Pattern
            .compile("\"(password|token|secret|credit_card|cvv)\"\\s*:\\s*\"([^\"]+)\"");

    private final AuditLogService auditLogService;

    public AuditAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Pointcut("within(com.posapi..*) && @annotation(com.posapi.infrastructure.aspect.LoggableAction)")
    public void loggableMethods() {
    }

    @Around(value = "loggableMethods()", argNames = "joinPoint")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        try {
            result = joinPoint.proceed();
            LoggableAction annotation = getAnnotation(joinPoint);
            saveLog(joinPoint, annotation, "SUCCESS", null);
            return result;
        } catch (Exception ex) {
            LoggableAction annotation = getAnnotation(joinPoint);
            saveLog(joinPoint, annotation, "ERROR", ex.getMessage());
            throw ex;
        }
    }

    private void saveLog(JoinPoint joinPoint, LoggableAction loggableAction, String status, String error) {
        try {
            String user = getCurrentUser();
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

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
        } catch (RuntimeException e) {
            logger.error("Failed to save audit log", e);
        }
    }

    private LoggableAction getAnnotation(JoinPoint joinPoint) {
        if (joinPoint.getSignature() instanceof MethodSignature methodSignature) {
            var method = methodSignature.getMethod();
            return method != null ? AnnotationUtils.findAnnotation(method, LoggableAction.class) : null;
        }
        return null;
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
        if (payload == null)
            return null;
        return MASK_PATTERN.matcher(payload).replaceAll("\"$1\":\"****\"");
    }

    private String truncatePayload(String payload) {
        if (payload != null && payload.length() > MAX_PAYLOAD_LENGTH) {
            return payload.substring(0, MAX_PAYLOAD_LENGTH - 3) + "...";
        }
        return payload;
    }
}