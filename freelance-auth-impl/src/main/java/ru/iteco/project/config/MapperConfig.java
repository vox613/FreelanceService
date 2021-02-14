package ru.iteco.project.config;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.iteco.project.domain.User;
import ru.iteco.project.resource.dto.UserBaseDto;
import ru.iteco.project.resource.dto.UserDtoRequest;
import ru.iteco.project.resource.dto.UserDtoResponse;
import ru.iteco.project.service.mappers.DateTimeMapper;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;


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
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("dateTimeFormatter", new DateTimeFormatter());
        userMapperConfigure(mapperFactory);
    }


    /**
     * Метод конфигурирует маппер для преобразований: User --> UserDtoResponse,
     * UserDtoRequest --> User, User --> UserBaseDto
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void userMapperConfigure(MapperFactory mapperFactory) {
        // GET  User --> UserDtoResponse
        mapperFactory
                .classMap(User.class, UserDtoResponse.class)
                .byDefault()
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();

        // POST/PUT  UserDtoRequest --> User
        mapperFactory
                .classMap(UserDtoRequest.class, User.class)
                .byDefault()
                .register();

        // GET  User --> UserBaseDto
        mapperFactory
                .classMap(User.class, UserBaseDto.class)
                .byDefault()
                .register();
    }

    /**
     * Класс-конвертер для преобразования типов и форматов дат
     */
    static class DateTimeFormatter extends BidirectionalConverter<LocalDateTime, String> {

        @Override
        public String convertTo(LocalDateTime source, Type<String> destinationType, MappingContext mappingContext) {
            return DateTimeMapper.objectToString(source);
        }

        @Override
        public LocalDateTime convertFrom(String source, Type<LocalDateTime> destinationType, MappingContext mappingContext) {
            return DateTimeMapper.stringToObject(source);
        }
    }
}
