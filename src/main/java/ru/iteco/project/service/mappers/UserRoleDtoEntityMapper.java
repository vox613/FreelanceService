package ru.iteco.project.service.mappers;

import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.UserRoleDtoRequest;
import ru.iteco.project.controller.dto.UserRoleDtoResponse;
import ru.iteco.project.domain.UserRole;

import java.util.UUID;

/**
 * Класс маппер для сущности UserRole
 */
@Service
public class UserRoleDtoEntityMapper implements DtoEntityMapper<UserRole, UserRoleDtoRequest, UserRoleDtoResponse> {

    @Override
    public UserRoleDtoResponse entityToResponseDto(UserRole entity) {
        UserRoleDtoResponse userRoleDtoResponse = new UserRoleDtoResponse();
        if (entity != null) {
            userRoleDtoResponse.setId(entity.getId());
            userRoleDtoResponse.setValue(entity.getValue());
            userRoleDtoResponse.setCreatedAt(DateTimeMapper.objectToString(entity.getCreatedAt()));
            userRoleDtoResponse.setUpdatedAt(DateTimeMapper.objectToString(entity.getUpdatedAt()));
        }
        return userRoleDtoResponse;
    }


    @Override
    public UserRole requestDtoToEntity(UserRoleDtoRequest requestDto) {
        UserRole userRole = new UserRole();
        if (requestDto != null) {
            userRole.setId(UUID.randomUUID());
            userRole.setValue(requestDto.getValue());
        }
        return userRole;
    }
}
