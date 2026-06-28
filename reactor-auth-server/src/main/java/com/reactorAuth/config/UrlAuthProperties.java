package com.reactorAuth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.security.url-auth")
public class UrlAuthProperties {
    /** 是否开启基于 URL 的权限验证 */
    private boolean enabled = false;
}
