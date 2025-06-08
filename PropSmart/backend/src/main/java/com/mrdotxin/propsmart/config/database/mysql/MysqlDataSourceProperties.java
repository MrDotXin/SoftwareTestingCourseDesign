package com.mrdotxin.propsmart.config.database.mysql;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource.mysql") // 绑定二级前缀
public class MysqlDataSourceProperties {
    private String url;
    private String driverClassName;
    private String username;
    private String password;
    private HikariCPProperties hikari; // 嵌套属性（HikariCP 配置）

    // 嵌套 HikariCP 配置类
    @Data
    public static class HikariCPProperties {
        private int maximumPoolSize;
        private int minimumIdle;
        // 添加其他 HikariCP 配置属性（如 connectionTimeout 等）
    }
}