package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ForecastDto(
        @JsonProperty("list") List<ForecastItem> items
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ForecastItem(
            @JsonProperty("dt_txt") String dtTxt,
            @JsonProperty("weather") List<Weather> weather,
            @JsonProperty("pop") Double pop
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Weather(
            String main
    ) {}
}