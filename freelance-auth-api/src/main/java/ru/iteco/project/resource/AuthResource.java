package ru.iteco.project.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.iteco.project.resource.dto.AuthUserDto;

import java.io.Serializable;

@RequestMapping("/api/v1/auth")
@Api(value = "API для получения токена аутенификации")
public interface AuthResource {

    /**
     * Контроллер для получения пользователем токена аутентификации
     *
     * @param authUserDto - модель с данными для осуществления аутентификации
     * @return - токен атентификации для пользователя
     */
    @PostMapping
    @ApiOperation(value = "Получение токена аутенификации пользователя в системе")
    ResponseEntity<? extends Serializable> createToken(@Validated @RequestBody AuthUserDto authUserDto, BindingResult result);
}
