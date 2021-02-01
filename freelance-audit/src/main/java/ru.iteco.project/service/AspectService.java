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
import ru.iteco.project.resource.dto.AuditEventDto;
import ru.iteco.project.enumaration.AuditCode;
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

        AuditEventDto auditEventDto = prepareBaseAuditData(annotation.operation());
        auditService.createAuditEvent(prepareStartEvent(auditEventDto, joinPoint));
        try {
            Object proceed = joinPoint.proceed();
            prepareSuccessEvent(auditEventDto, proceed);
            return proceed;
        } catch (Throwable e) {
            prepareFailureEvent(auditEventDto, e);
            throw e;
        } finally {
            auditService.createAuditEvent(auditEventDto);
        }
    }


    private AuditEventDto prepareBaseAuditData(AuditCode auditCode) {
        AuditEventDto auditEventDto = new AuditEventDto();
        auditEventDto.setId(UUID.randomUUID());
        auditEventDto.setAuditCode(auditCode);
        auditEventDto.setTimeStart(LocalDateTime.now());

        auditEventDto.setUserName("");
        return auditEventDto;
    }

    private AuditEventDto prepareStartEvent(AuditEventDto auditEventDto, ProceedingJoinPoint joinPoint) {

        auditEventDto.setAuditEventType(AuditEventType.START);

        for (Object arg : joinPoint.getArgs()) {
            if (!(arg instanceof BeanPropertyBindingResult || arg instanceof UriBuilder)) {
                auditEventDto.getParams().put(arg.getClass().getSimpleName(), arg);
            }
        }
        return auditEventDto;
    }

    private void prepareSuccessEvent(AuditEventDto auditEventDto, Object returnValue) {
        auditEventDto.setAuditEventType(AuditEventType.SUCCESS);
        auditEventDto.setTimeEnd(LocalDateTime.now());
        auditEventDto.getReturnValue().put(returnValue.getClass().getSimpleName(), returnValue);
    }

    private void prepareFailureEvent(AuditEventDto auditEventDto, Throwable e) {
        auditEventDto.setAuditEventType(AuditEventType.FAILURE);
        auditEventDto.setTimeEnd(LocalDateTime.now());
        auditEventDto.getReturnValue().put("exception_msg", e.getMessage());
    }

}
