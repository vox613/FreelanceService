package ru.iteco.project.config.mapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.iteco.project.config.mapper.converter.DateTimeConverter;
import ru.iteco.project.config.mapper.converter.PasswordConverter;
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

    private final DateTimeConverter dateTimeConverter;
    private final PasswordConverter passwordConverter;

    public MapperConfig(DateTimeConverter dateTimeConverter, PasswordConverter passwordConverter) {
        this.dateTimeConverter = dateTimeConverter;
        this.passwordConverter = passwordConverter;
    }

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
        converterFactory.registerConverter("dateTimeFormatter", dateTimeConverter);
        converterFactory.registerConverter("passwordConverter", passwordConverter);
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
                .fieldMap("password", "password")
                .aToB().converter("passwordConverter").add()
                .byDefault()
                .register();

        // GET  User --> UserBaseDto
        mapperFactory
                .classMap(User.class, UserBaseDto.class)
                .byDefault()
                .register();
    }
}
