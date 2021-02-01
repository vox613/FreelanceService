package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.CurrencyInfo;
import ru.iteco.project.exception.CurrencyNotExistException;
import ru.iteco.project.repository.ExchangeRatesRepository;
import ru.iteco.project.resource.dto.ConversionDto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Класс реализует функционал сервисного слоя для конвертации валюты
 */
@Service
public class CurrencyConverterServiceImpl implements CurrencyConverterService {
    private static final Logger log = LogManager.getLogger(CurrencyConverterServiceImpl.class.getName());

    /*** Количество цифр после запятой*/
    @Value("${currency.scale}")
    private Integer scale;

    /*** Масштаб режима округления*/
    @Value("${currency.rounding}")
    private Integer rounding;

    /*** Объект доступа к репозиторию валют */
    private final ExchangeRatesRepository exchangeRatesRepository;

    public CurrencyConverterServiceImpl(ExchangeRatesRepository exchangeRatesRepository) {
        this.exchangeRatesRepository = exchangeRatesRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ConversionDto convert(ConversionDto conversionDto) {
        Optional<CurrencyInfo> fromCurrencyOpt = exchangeRatesRepository.findByCharCode(conversionDto.getFromCurrency());
        Optional<CurrencyInfo> toCurrencyOpt = exchangeRatesRepository.findByCharCode(conversionDto.getToCurrency());

        if (fromCurrencyOpt.isPresent() && toCurrencyOpt.isPresent()) {
            CurrencyInfo fromCurrencyInfo = fromCurrencyOpt.get();
            CurrencyInfo toCurrencyInfo = toCurrencyOpt.get();
            calculate(conversionDto, fromCurrencyInfo, toCurrencyInfo);
            return conversionDto;
        }
        throw new CurrencyNotExistException("Not exist currency!");
    }


    @Override
    public Boolean isValidCurrency(String currency) {
        return exchangeRatesRepository.existsByCharCode(currency);
    }

    private void calculate(ConversionDto conversionDto, CurrencyInfo fromCurrency, CurrencyInfo toCurrency) {
        log.debug("Trying convert from {} to {}", fromCurrency.getCharCode(), toCurrency.getCharCode());

        BigDecimal sumsRatio = fromCurrency.getCurrentValue()
                .divide(toCurrency.getCurrentValue(), scale, RoundingMode.valueOf(rounding));
        BigDecimal nominalRatio = toCurrency.getNominal()
                .divide(fromCurrency.getNominal(), scale, RoundingMode.valueOf(rounding));
        BigDecimal amount = conversionDto.getAmount();
        conversionDto.setConvertedAmount(amount.multiply(sumsRatio).multiply(nominalRatio, new MathContext(rounding)));

        log.debug("Success convert {} {} to {} {}", amount, fromCurrency.getCharCode(),
                conversionDto.getConvertedAmount(), toCurrency.getCharCode());
    }

}
