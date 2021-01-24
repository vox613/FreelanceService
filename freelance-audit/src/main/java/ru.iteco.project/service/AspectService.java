package ru.iteco.project.service;

import org.apache.commons.lang3.Validate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.util.UriBuilder;
import ru.iteco.project.annotation.Audit;
import ru.iteco.project.domain.AuditEvent;
import ru.iteco.project.enumaration.AuditEventType;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс реализует функционал сервисного слоя для работы с аспектами
 */
@Aspect
@Component
public class AspectService {

    /*** Объект сервисного слоя событий аудита */
    private final AuditService auditService;

    public AspectService(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Срез события для всех public методов аннотированных @Audit
     */
    @Pointcut("@annotation(ru.iteco.project.annotation.Audit) && execution(public * * (..))")
    public void auditPointcut() {
    }

    /**
     * Совет/обработчик для аннотации @Audit
     *
     * @param joinPoint - точка соединения, место, где начинаются определённые действия модуля АОП
     * @return - результат выполнения вызванного метода или его необходимой модификации
     * @throws Throwable - исключительная ситуация в процессе обработки
     */
    @Around("auditPointcut()")
    public Object writeAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Audit annotation = method.getAnnotation(Audit.class);
        Validate.notNull(annotation);

        String operation = annotation.operation();
        UUID uuid = UUID.randomUUID();

        auditService.createAuditEvent(prepareStartEvent(uuid, operation, joinPoint));
        AuditEvent auditEvent = null;
        try {
            Object proceed = joinPoint.proceed();
            auditEvent = prepareSuccessEvent(uuid, operation, proceed);
            return proceed;
        } catch (Throwable e) {
            auditEvent = prepareFailureEvent(uuid, operation, e);
            throw e;
        } finally {
            auditService.createAuditEvent(auditEvent);
        }
    }


    private AuditEvent prepareBaseAuditData(UUID uuid, String auditCode) {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setId(uuid);
        auditEvent.setAuditCode(auditCode);
        auditEvent.setTimeStart(LocalDateTime.now());

        auditEvent.setUserName("");
        return auditEvent;
    }

    private AuditEvent prepareStartEvent(UUID uuid, String operation, ProceedingJoinPoint joinPoint) {
        AuditEvent auditEvent = prepareBaseAuditData(uuid, operation);
        auditEvent.setAuditEventType(AuditEventType.START);
        for (Object arg : joinPoint.getArgs()) {
            if (!(arg instanceof BeanPropertyBindingResult || arg instanceof UriBuilder)) {
                auditEvent.getParams().put(arg.getClass().getSimpleName(), arg);
            }
        }
        return auditEvent;
    }

    private AuditEvent prepareSuccessEvent(UUID uuid, String operation, Object returnValue) {
        AuditEvent auditEvent = prepareBaseAuditData(uuid, operation);
        auditEvent.setAuditEventType(AuditEventType.SUCCESS);
        auditEvent.setTimeEnd(LocalDateTime.now());
        auditEvent.getReturnValue().put(returnValue.getClass().getSimpleName(), returnValue);
        return auditEvent;
    }

    private AuditEvent prepareFailureEvent(UUID uuid, String operation, Throwable e) {
        AuditEvent auditEvent = prepareBaseAuditData(uuid, operation);
        auditEvent.setAuditEventType(AuditEventType.FAILURE);
        auditEvent.setTimeEnd(LocalDateTime.now());
        auditEvent.getReturnValue().put("exception_msg", e.getMessage());
        return auditEvent;
    }

}
