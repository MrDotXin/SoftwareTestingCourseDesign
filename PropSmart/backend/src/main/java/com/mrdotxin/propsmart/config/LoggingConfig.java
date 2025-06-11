package com.mrdotxin.propsmart.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Bean
    public Filter<ILoggingEvent> sqlSessionLoggingFilter() {
        return new Filter<ILoggingEvent>() {
            @Override
            public FilterReply decide(ILoggingEvent event) {
                String message = event.getMessage();
                // 抑制特定的SqlSession同步警告
                if (message != null &&
                    (message.contains("SqlSession") && message.contains("was not registered for synchronization")) ||
                    (message.contains("JDBC Connection") && message.contains("will not be managed by Spring"))) {
                    return FilterReply.DENY;
                }
                return FilterReply.NEUTRAL;
            }
        };
    }
}
