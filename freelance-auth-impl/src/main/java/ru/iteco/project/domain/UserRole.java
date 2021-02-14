package ru.iteco.project.domain;

public enum UserRole {

    ADMIN("Администратор"),
    USER("Пользователь");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
     *
     * @param inputUserRole - текстовое представление роли пользователя
     * @return true - в перечислении присутствует аргумент с данным именем,
     * false - в перечислении отсутствует аргумент с данным именем
     */
    public static boolean isCorrectValue(String inputUserRole) {
        for (UserRole userRole : values()) {
            if (userRole.name().equals(inputUserRole)) {
                return true;
            }
        }
        return false;
    }
}
