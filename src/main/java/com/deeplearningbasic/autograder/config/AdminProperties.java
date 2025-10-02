package com.deeplearningbasic.autograder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "admin")
@Getter
@Setter
public class AdminProperties {
    private List<String> emails;
}
