package ru.iteco.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.iteco.project.domain.CurrencyInfo;
import ru.iteco.project.logger.utils.LoggerUtils;
import ru.iteco.project.repository.ExchangeRatesRepository;
import ru.iteco.project.resource.dto.ExchangeRatesDtoResponse;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Класс реализует функционал сервисного слоя для работы с отложенными заданиями и заданиями по CRON расписанию
 */
@Service
public class SchedulerServiceImpl implements SchedulerService {
    private static final Logger log = LogManager.getLogger(SchedulerServiceImpl.class.getName());

    /*** URL адрес источника информации о курсах валют*/
    @Value("${urls.exchangeRatesUrl}")
    private String url;


    /*** Объект доступа к репозиторию валют */
    private final ExchangeRatesRepository exchangeRatesRepository;

    /*** Объект маппера dto <-> сущность */
    private final MapperFacade mapperFacade;

    /*** Self ссылка на содержащий класс */
    private SchedulerService schedulerServiceSelf;


    @Lazy
    @Autowired
    public void setSchedulerServiceSelf(SchedulerService schedulerServiceSelf) {
        this.schedulerServiceSelf = schedulerServiceSelf;
    }

    public SchedulerServiceImpl(ExchangeRatesRepository exchangeRatesRepository, MapperFacade mapperFacade) {
        this.exchangeRatesRepository = exchangeRatesRepository;
        this.mapperFacade = mapperFacade;
    }

    @Override
    public void updatingExchangeRatesTask() {
        ExchangeRatesDtoResponse exchangeRates = getExchangeRates();
        if (exchangeRates == null) {
            log.error("Error getting updated currency values! Skipping an update iteration!");
            return;
        }

        Map<String, CurrencyInfo> convertedValues = exchangeRates.getValues().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> mapperFacade.map(entry.getValue(), CurrencyInfo.class)));

        schedulerServiceSelf.updateExchangeRates(convertedValues);
        log.info("Exchange rate data has been successfully updated!");
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateExchangeRates(Map<String, CurrencyInfo> newCurrencyInfos) {
        if (exchangeRatesRepository.count() == 0) {
            exchangeRatesRepository.saveAll(newCurrencyInfos.values());
        } else {
            List<CurrencyInfo> oldCurrencyInfos = exchangeRatesRepository.findAll();

            oldCurrencyInfos.stream()
                    .filter(currencyInfo -> newCurrencyInfos.containsKey(currencyInfo.getCharCode()))
                    .peek(currencyInfo -> mapperFacade.map(newCurrencyInfos.get(currencyInfo.getCharCode()), currencyInfo))
                    .map(exchangeRatesRepository::save)
                    .forEach(currencyInfo -> newCurrencyInfos.remove(currencyInfo.getCharCode()));

            newCurrencyInfos.values().forEach(exchangeRatesRepository::save);
        }
    }


    private ExchangeRatesDtoResponse getExchangeRates() {
        LoggerUtils.beforeCall(Level.DEBUG, "getExchangeRates()", "{}");
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        ExchangeRatesDtoResponse exchangeRatesDtoResponse = null;
        try {
            RequestEntity<Void> build = RequestEntity.get(new URI(url)).build();
            ResponseEntity<String> response = restTemplate.exchange(build, String.class);
            String body = response.getBody();
            exchangeRatesDtoResponse = objectMapper.readValue(body, ExchangeRatesDtoResponse.class);
        } catch (Exception e) {
            log.error("Error executing a request to the currency service!", e);
            return exchangeRatesDtoResponse;
        }
        LoggerUtils.afterCall(Level.DEBUG, "getExchangeRates()", exchangeRatesDtoResponse);
        return exchangeRatesDtoResponse;
    }

}
