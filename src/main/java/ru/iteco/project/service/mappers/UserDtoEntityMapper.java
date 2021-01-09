package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.domain.User;
import ru.iteco.project.domain.UserStatus;
import ru.iteco.project.exception.InvalidUserRoleException;
import ru.iteco.project.exception.InvalidUserStatusException;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.repository.UserRoleRepository;
import ru.iteco.project.repository.UserStatusRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Класс маппер для сущности User
 */
@Service
public class UserDtoEntityMapper implements DtoEntityMapper<User, UserDtoRequest, UserDtoResponse> {

    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию ролей пользователей */
    private final UserRoleRepository userRoleRepository;

    /*** Объект доступа к репозиторию статусов пользователей */
    private final UserStatusRepository userStatusRepository;

    @Value("${errors.user.role.operation.unavailable}")
    private String unavailableOperationMessage;

    @Value("${errors.user.role.invalid}")
    private String userRoleIsInvalidMessage;

    @Value("${errors.user.status.invalid}")
    private String userStatusIsInvalidMessage;


    public UserDtoEntityMapper(TaskRepository taskRepository, UserRoleRepository userRoleRepository,
                               UserStatusRepository userStatusRepository) {
        this.taskRepository = taskRepository;
        this.userRoleRepository = userRoleRepository;
        this.userStatusRepository = userStatusRepository;
    }

    @Override
    public UserDtoResponse entityToResponseDto(User entity) {
        final UserDtoResponse userDtoResponse = new UserDtoResponse();
        if (entity != null) {
            userDtoResponse.setId(entity.getId());
            userDtoResponse.setFirstName(entity.getFirstName());
            userDtoResponse.setSecondName(entity.getSecondName());
            userDtoResponse.setLastName(entity.getLastName());
            userDtoResponse.setEmail(entity.getEmail());
            userDtoResponse.setPhoneNumber(entity.getPhoneNumber());
            userDtoResponse.setLogin(entity.getLogin());
            userDtoResponse.setRole(entity.getRole().getValue());
            userDtoResponse.setUserStatus(entity.getUserStatus().getValue());
            userDtoResponse.setWallet(entity.getWallet());
            userDtoResponse.setCreatedAt(DateTimeMapper.objectToString(entity.getCreatedAt()));
            userDtoResponse.setUpdatedAt(DateTimeMapper.objectToString(entity.getUpdatedAt()));
            taskRepository.findTasksByUser(entity)
                    .forEach(task -> userDtoResponse.getTasksIdList().add(task.getId()));
        }
        return userDtoResponse;
    }


    @Override
    public User requestDtoToEntity(UserDtoRequest requestDto) {
        User user = new User();
        if (requestDto != null) {
            user.setId(UUID.randomUUID());
            user.setFirstName(requestDto.getFirstName());
            user.setSecondName(requestDto.getSecondName());
            user.setLastName(requestDto.getLastName());
            user.setLogin(requestDto.getLogin());
            user.setPassword(requestDto.getPassword());
            user.setEmail(requestDto.getEmail());
            user.setPhoneNumber(requestDto.getPhoneNumber());
            user.setRole(userRoleRepository.findUserRoleByValue(requestDto.getRole())
                    .orElseThrow(() -> new InvalidUserRoleException(userRoleIsInvalidMessage)));
            user.setUserStatus(userStatusRepository.findUserStatusByValue(requestDto.getUserStatus())
                    .orElseThrow(() -> new InvalidUserStatusException(unavailableOperationMessage)));
            user.setWallet(requestDto.getWallet());
        }
        return user;
    }

    /**
     * Метод устанавливает переданному пользователю статус соответствующий переданному объекту userStatusEnum
     *
     * @param user           - сущность пользователя
     * @param userStatusEnum - объект перечисления статусов пользователей
     */
    public void updateUserStatus(User user, UserStatus.UserStatusEnum userStatusEnum) {
        Optional<UserStatus> userStatusByValue = userStatusRepository.findUserStatusByValue(userStatusEnum.name());
        UserStatus userStatus = userStatusByValue
                .orElseThrow(() -> new InvalidUserStatusException(userStatusIsInvalidMessage));
        user.setUserStatus(userStatus);
    }

    public UserRoleRepository getUserRoleRepository() {
        return userRoleRepository;
    }

    public UserStatusRepository getUserStatusRepository() {
        return userStatusRepository;
    }
}
