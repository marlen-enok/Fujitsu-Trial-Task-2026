package com.fujitsu.trial.dto.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Station {

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "wmocode")
    private String wmoCode;

    @JacksonXmlProperty(localName = "airtemperature")
    private Double airTemperature;

    @JacksonXmlProperty(localName = "windspeed")
    private Double windSpeed;

    @JacksonXmlProperty(localName = "phenomenon")
    private String weatherPhenomenon;
}