package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iteco.project.domain.CurrencyInfo;

import java.util.Optional;


/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности Currency
 */
public interface ExchangeRatesRepository extends JpaRepository<CurrencyInfo, String> {

    /**
     * Метод получения объекта CurrencyInfo из бд по текстовому коду валюты
     * @param charCode - текстовая аббревиатура валюты
     * @return - Optional объеккт с результатом запроса
     */
    Optional<CurrencyInfo> findByCharCode(String charCode);

    /**
     * Метод проверяет существование в бд валюты по текстовому коду валюты
     * @param charCode - текстовая аббревиатура валюты
     * @return true -валюта существует, false - данная валюта отсутствует
     */
    Boolean existsByCharCode(String charCode);

}
