package ru.iteco.project.config;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.iteco.project.domain.CurrencyInfo;
import ru.iteco.project.resource.dto.ExchangeRatesDtoResponse;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;


/**
 * Класс - конфигурация для Orika маппера
 */
@Configuration
public class MapperConfig implements OrikaMapperFactoryConfigurer {

    @Bean
    DatatypeFactory datatypeFactory() throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance();
    }

    @Bean
    MappingContext.Factory mappingFactory() {
        MappingContext.Factory factory = new MappingContext.Factory();
        new DefaultMapperFactory.Builder().mappingContextFactory(factory).build();
        return factory;
    }


    @Override
    public void configure(MapperFactory mapperFactory) {
        exchangeRatesConfigure(mapperFactory);
    }


    /**
     * Метод конфигурирует маппер для преобразований: CurrencyData --> ExchangeRatesDtoResponse,
     * ExchangeRatesDtoResponse --> CurrencyData
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void exchangeRatesConfigure(MapperFactory mapperFactory) {
        mapperFactory
                .classMap(ExchangeRatesDtoResponse.CurrencyData.class, CurrencyInfo.class)
                .byDefault()
                .register();

        mapperFactory
                .classMap(CurrencyInfo.class, CurrencyInfo.class)
                .exclude("id")
                .exclude("createdAt")
                .exclude("updatedAt")
                .byDefault()
                .register();
    }
}
