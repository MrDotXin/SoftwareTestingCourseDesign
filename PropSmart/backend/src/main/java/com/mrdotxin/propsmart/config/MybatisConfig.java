package com.mrdotxin.propsmart.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.mrdotxin.propsmart.config.typehandler.MysqlGeoTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis configuration
 */
@Configuration
@MapperScan("com.mrdotxin.propsmart.mapper")
public class MybatisConfig {

    /**
     * Register pagination interceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * Register Geometry type handler
     */
    @Bean
    public MysqlGeoTypeHandler mysqlGeoTypeHandler() {
        return new MysqlGeoTypeHandler();
    }
} 