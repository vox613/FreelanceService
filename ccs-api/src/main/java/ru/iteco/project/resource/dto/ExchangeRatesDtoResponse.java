package ru.iteco.project.resource.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Данные модели курса валют, получаемых из внешнего ресурса")
public class ExchangeRatesDtoResponse implements Serializable {

    @JsonProperty("Date")
    @ApiModelProperty(value = "Дата обновления информации на ресурсе", example = "2021-01-30T11:30:00+03:00", required = true)
    private String date;

    @JsonProperty("PreviousDate")
    @ApiModelProperty(value = "Предыдущая дата обновления информации на ресурсе", example = "2021-01-30T11:30:00+03:00", required = true)
    private String previousDate;

    @JsonProperty("PreviousURL")
    @ApiModelProperty(value = "URL для доступа к архивным данным", example = "\\/\\/www.cbr-xml-daily.ru\\/archive\\/2021\\/01\\/29\\/daily_json.js", required = true)
    private String previousURL;

    @JsonProperty("Timestamp")
    @ApiModelProperty(value = "Дата выполнения запроса к ресурсу", example = "2021-01-30T11:30:00+03:00", required = true)
    private String timestamp;

    @JsonProperty("Valute")
    @ApiModelProperty(value = "Список информации о курсах валют",
            example = "\"Valute\": {\n" +
                    "        \"AUD\": {\n" +
                    "            \"ID\": \"R01010\",\n" +
                    "            \"NumCode\": \"036\",\n" +
                    "            \"CharCode\": \"AUD\",\n" +
                    "            \"Nominal\": 1,\n" +
                    "            \"Name\": \"Австралийский доллар\",\n" +
                    "            \"Value\": 58.3333,\n" +
                    "            \"Previous\": 58.038\n" +
                    "        }\n" +
                    "        }",
            required = true)
    private Map<String, CurrencyData> values;


    public ExchangeRatesDtoResponse() {
    }


    public ExchangeRatesDtoResponse(String date, String previousDate, String previousURL, String timestamp, Map<String,
            CurrencyData> values) {
        this.date = date;
        this.previousDate = previousDate;
        this.previousURL = previousURL;
        this.timestamp = timestamp;
        this.values = values;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPreviousDate() {
        return previousDate;
    }

    public void setPreviousDate(String previousDate) {
        this.previousDate = previousDate;
    }

    public String getPreviousURL() {
        return previousURL;
    }

    public void setPreviousURL(String previousURL) {
        this.previousURL = previousURL;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, CurrencyData> getValues() {
        return values;
    }

    public void setValues(Map<String, CurrencyData> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "ExchangeRatesDtoResponse{" +
                "date='" + date + '\'' +
                ", previousDate='" + previousDate + '\'' +
                ", previousURL='" + previousURL + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", values=" + values +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @ApiModel(description = "Данные модели информации о валюте")
    public static class CurrencyData {

        @JsonProperty("ID")
        @ApiModelProperty(value = "Уникальный идентификатор валюты", example = "R01010", required = true)
        private String id;

        @JsonProperty("NumCode")
        @ApiModelProperty(value = "Уникальный цифровой идентификатор валюты", example = "036", required = true)
        private String numCode;

        @JsonProperty("CharCode")
        @ApiModelProperty(value = "Текстовая аббревиатура валюты", example = "036", required = true)
        private String charCode;

        @JsonProperty("Nominal")
        @ApiModelProperty(value = "Номинал валюты", example = "1", required = true)
        private BigDecimal nominal;

        @JsonProperty("Name")
        @ApiModelProperty(value = "Текстовое описание валюты", example = "Австралийский доллар", required = true)
        private String name;

        @JsonProperty("Value")
        @ApiModelProperty(value = "Текущий курс валюты относительно рубля", example = "58.3333", required = true)
        private BigDecimal currentValue;

        @JsonProperty("Previous")
        @ApiModelProperty(value = "Предыдущий курс валюты относительно рубля", example = "58.038", required = true)
        private BigDecimal previousValue;


        public CurrencyData() {
        }

        public CurrencyData(String id, String numCode, String charCode, BigDecimal nominal, String name,
                            BigDecimal currentValue, BigDecimal previousValue) {
            this.id = id;
            this.numCode = numCode;
            this.charCode = charCode;
            this.nominal = nominal;
            this.name = name;
            this.currentValue = currentValue;
            this.previousValue = previousValue;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNumCode() {
            return numCode;
        }

        public void setNumCode(String numCode) {
            this.numCode = numCode;
        }

        public String getCharCode() {
            return charCode;
        }

        public void setCharCode(String charCode) {
            this.charCode = charCode;
        }

        public BigDecimal getNominal() {
            return nominal;
        }

        public void setNominal(BigDecimal nominal) {
            this.nominal = nominal;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getCurrentValue() {
            return currentValue;
        }

        public void setCurrentValue(BigDecimal currentValue) {
            this.currentValue = currentValue;
        }

        public BigDecimal getPreviousValue() {
            return previousValue;
        }

        public void setPreviousValue(BigDecimal previousValue) {
            this.previousValue = previousValue;
        }


        @Override
        public String toString() {
            return "CurrencyData{" +
                    "id='" + id + '\'' +
                    ", numCode='" + numCode + '\'' +
                    ", charCode='" + charCode + '\'' +
                    ", nominal=" + nominal +
                    ", name='" + name + '\'' +
                    ", currentValue=" + currentValue +
                    ", previousValue=" + previousValue +
                    '}';
        }
    }

}
