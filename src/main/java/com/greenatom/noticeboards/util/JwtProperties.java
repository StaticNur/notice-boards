package com.greenatom.noticeboards.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * POJO for jwt properties
 */
@Component
@Data
@PropertySource(value = "classpath:application.yml", factory = YamlPropertiesUtilFactory.class)
public class JwtProperties {
    @Value("${spring.jwt.secret}")
    private String secret;
    @Value("${spring.jwt.access}")
    private Long access;
    @Value("${spring.jwt.refresh}")
    private Long refresh;
}
