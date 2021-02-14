package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@PropertySource(value = {"classpath:application.yml"})
public class DateTimeMapper {

    /*** Установленный формат даты и времени*/
    private static String formatDateTime;

    @Value("${format.date.time}")
    public void setFormatDateTime(String formatDateTime) {
        DateTimeMapper.formatDateTime = formatDateTime;
    }


    /**
     * Метод конвертирует объект localDateTime в строку и приводит ее к установленному формату
     *
     * @param localDateTime - объект LocalDateTime
     * @return строковое представвление переданной даты, приведенное к заданному формату
     */
    public static String objectToString(LocalDateTime localDateTime) {
        String dateTimeStr = "";
        if (localDateTime != null) {
            dateTimeStr = localDateTime.format(DateTimeFormatter.ofPattern(formatDateTime));
        }
        return dateTimeStr;
    }

    /**
     * Метод конвертирует строковое, форматированное представление даты и времени в объект localDateTime
     *
     * @param dateTimeStr - строковое представление даты и времени в установленном формате (yyyy-MM-dd HH:mm:ss)
     * @return - объект LocalDateTime с заданным временем и датой
     */
    public static LocalDateTime stringToObject(String dateTimeStr) {
        if (dateTimeStr != null) {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(formatDateTime));
        }
        return null;
    }

}
