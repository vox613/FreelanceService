package ru.iteco.project.service.mappers;

import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.UserStatusDtoRequest;
import ru.iteco.project.controller.dto.UserStatusDtoResponse;
import ru.iteco.project.domain.UserStatus;

import java.util.UUID;

/**
 * Класс маппер для сущности TaskStatus
 */
@Service
public class UserStatusDtoEntityMapper implements DtoEntityMapper<UserStatus, UserStatusDtoRequest, UserStatusDtoResponse> {

    @Override
    public UserStatusDtoResponse entityToResponseDto(UserStatus entity) {
        UserStatusDtoResponse userStatusDtoResponse = new UserStatusDtoResponse();
        if (entity != null) {
            userStatusDtoResponse.setId(entity.getId());
            userStatusDtoResponse.setValue(entity.getValue());
            userStatusDtoResponse.setDescription(entity.getDescription());
            userStatusDtoResponse.setCreatedAt(DateTimeMapper.objectToString(entity.getCreatedAt()));
            userStatusDtoResponse.setUpdatedAt(DateTimeMapper.objectToString(entity.getUpdatedAt()));
        }
        return userStatusDtoResponse;
    }

    @Override
    public UserStatus requestDtoToEntity(UserStatusDtoRequest requestDto) {
        UserStatus userStatus = new UserStatus();
        if (requestDto != null) {
            userStatus.setId(UUID.randomUUID());
            userStatus.setValue(requestDto.getValue());
            userStatus.setDescription(requestDto.getDescription());
        }
        return userStatus;
    }
}
