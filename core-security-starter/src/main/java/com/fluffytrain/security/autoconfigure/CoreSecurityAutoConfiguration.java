package com.fluffytrain.security.autoconfigure;

import com.fluffytrain.security.core.config.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@ComponentScan(basePackages = "com.fluffytrain.security.core")
public class CoreSecurityAutoConfiguration {
}
