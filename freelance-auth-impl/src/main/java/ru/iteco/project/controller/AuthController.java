package ru.iteco.project.controller;

import org.apache.logging.log4j.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import ru.iteco.project.annotation.Audit;
import ru.iteco.project.resource.AuthResource;
import ru.iteco.project.resource.dto.AuthUserDto;
import ru.iteco.project.resource.dto.TokenDto;
import ru.iteco.project.service.AuthService;
import ru.iteco.project.validator.AuthDtoValidator;

import java.io.Serializable;

import static ru.iteco.project.controller.audit.AuditCode.TOKEN_CREATE;
import static ru.iteco.project.utils.LoggerUtils.afterCall;
import static ru.iteco.project.utils.LoggerUtils.beforeCall;


@RestController
public class AuthController implements AuthResource {

    /*** Объект сервисного слоя для работы с процессом аутентификации пользователей*/
    private final AuthService authService;

    /*** Объект валидатора для AuthUserDto*/
    private final AuthDtoValidator authDtoValidator;


    public AuthController(AuthService authService, AuthDtoValidator authDtoValidator) {
        this.authService = authService;
        this.authDtoValidator = authDtoValidator;
    }

    @Override
    @Audit(operation = TOKEN_CREATE)
    public ResponseEntity<? extends Serializable> createToken(AuthUserDto authUserDto, BindingResult result) {
        beforeCall(Level.DEBUG, "createToken()", authUserDto);
        if (result.hasErrors()) {
            authUserDto.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(authUserDto);
        }
        TokenDto token = authService.generateToken(authUserDto);
        afterCall(Level.DEBUG, "createToken()", token);
        return ResponseEntity.ok(token);
    }


    @InitBinder(value = {"authUserDto"})
    public void initUserDtoRequestBinder(WebDataBinder binder) {
        binder.setValidator(authDtoValidator);
    }

}
