package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Базовая модель для получение токена")
public class TokenDto implements Serializable {

    @ApiModelProperty(value = "Токен доступа", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZTMiLCJhdWQiOiJmcmVlbGFuY2Utc2VydmljZSIsInJvbGUiOiJVU0VSIiwiaXNzIjoiZnJlZWxhbmNlLWF1dGgiLCJleHAiOjE2MTM1MTQ2MzAsImlhdCI6MTYxMzUxMTAzMCwiZW1haWwiOiJ1c2VybmFtZTNAZ21haWwuY29tIiwianRpIjoiMjk0MTVlYTgtZjhkNC00OTRkLTlhN2UtYzlhN2QxNjNhNTUwIn0.Gco3XZG7pENVyO14tTq7NaqLr46m29sX6zYBIqA0tF0", required = true)
    private String accessToken;


    public TokenDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public TokenDto() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
