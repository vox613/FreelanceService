package ru.iteco.project.controller.advice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import ru.iteco.project.exception.MismatchedIdException;
import ru.iteco.project.resource.dto.ClientDtoRequest;
import ru.iteco.project.resource.dto.ContractDtoRequest;
import ru.iteco.project.resource.dto.TaskDtoRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Класс-расширение функционала контроллеров
 */
@RestControllerAdvice
@PropertySource(value = {"classpath:errors.properties"}, encoding = "UTF-8")
public class CustomRequestBodyAdviceAdapter extends RequestBodyAdviceAdapter {
    private static final Logger log = LogManager.getLogger(CustomRequestBodyAdviceAdapter.class.getName());


    /*** Объект предоставляет информацию о запросе для сервлетов HTTP */
    private final HttpServletRequest httpServletRequest;

    /*** Текст ошибки несовпадающих id в pathVariable и теле запроса для метода PUT*/
    @Value("${errors.id.mismatched}")
    private String mismatchedIdMessage;


    public CustomRequestBodyAdviceAdapter(HttpServletRequest httpServletRequest) {
        super();
        this.httpServletRequest = httpServletRequest;
    }


    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return executableMethodHaveAnnotation(methodParameter, PutMapping.class);
    }


    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        Object attribute = httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attribute instanceof Map) {
            Map pathVariables = (Map) attribute;
            String pathVariableId = (String) pathVariables.get("id");

            boolean same = true;
            boolean instanceFound = false;

            if (body instanceof ClientDtoRequest) {
                ClientDtoRequest clientDtoRequestDtoRequest = (ClientDtoRequest) body;
                same = Objects.equals(pathVariableId, String.valueOf(clientDtoRequestDtoRequest.getId()));
                instanceFound = true;
            }
            if (!instanceFound && (body instanceof TaskDtoRequest)) {
                TaskDtoRequest taskDtoRequest = (TaskDtoRequest) body;
                same = Objects.equals(pathVariableId, String.valueOf(taskDtoRequest.getId()));
                instanceFound = true;
            }
            if (!instanceFound && (body instanceof ContractDtoRequest)) {
                ContractDtoRequest contractDtoRequest = (ContractDtoRequest) body;
                same = Objects.equals(pathVariableId, String.valueOf(contractDtoRequest.getId()));
            }

            if (!same) {
                throw new MismatchedIdException(mismatchedIdMessage);
            }
        }
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }


    private boolean executableMethodHaveAnnotation(MethodParameter methodParameter, Class<? extends Annotation> annotationClass) {
        boolean annotatedByPutMapping = false;
        try {
            String executableClassName = methodParameter.getContainingClass().getName();
            Class<?> executableClass = Class.forName(executableClassName);

            String executableMethodName = methodParameter.getExecutable().getName();
            Method executableMethod = executableClass.getMethod(executableMethodName, methodParameter.getExecutable().getParameterTypes());

            annotatedByPutMapping = AnnotationUtils.findAnnotation(executableMethod, annotationClass) != null;

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            log.error(e);
        }
        return annotatedByPutMapping;
    }

}
