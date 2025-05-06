package com.chungang.capstone.openstep.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "openai.api")
public class OpenAIConfig {
    private String key;
    private String url;

    public void setKey(String key) {
        this.key = key;
    }
}
