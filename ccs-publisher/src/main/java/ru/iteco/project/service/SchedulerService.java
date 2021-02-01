package ru.iteco.project.service;

import ru.iteco.project.domain.CurrencyInfo;

import java.util.Map;

/**
 * Интерфейс описывает общий функционал Service слоя для отложенных заданий и заданий по CRON расписанию
 */
public interface SchedulerService {

    /**
     * Метод выполнения отложенного задания обновления в БД текущих курсов валют
     */
    void updatingExchangeRatesTask();

    /**
     * Метод непосредственного обновления записей курсов валют в БД
     * @param currencyInfoList - подготовленная карта актуальных курсов
     */
    void updateExchangeRates(Map<String, CurrencyInfo> currencyInfoList);

}
