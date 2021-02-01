package ru.iteco.project.controller.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.iteco.project.resource.ExchangeRatesResource;


@FeignClient(name = "ccs-service", url = "http://localhost:8082/ccs")
public interface ExchangeRatesClient extends ExchangeRatesResource {
}
