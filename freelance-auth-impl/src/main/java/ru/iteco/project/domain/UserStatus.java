package ru.iteco.project.domain;

public enum UserStatus {

    CREATED("Создан"),
    ACTIVE("Активен"),
    DELETED("Удален"),
    BLOCKED("Заблокирован");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
     *
     * @param inputUserStatus - текстовое представление статуса пользователя
     * @return true - в перечислении присутствует аргумент с данным именем,
     * false - в перечислении отсутствует аргумент с данным именем
     */
    public static boolean isCorrectValue(String inputUserStatus) {
        for (UserStatus userStatus : values()) {
            if (userStatus.name().equals(inputUserStatus)) {
                return true;
            }
        }
        return false;
    }
}
