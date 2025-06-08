package com.mrdotxin.propsmart.config.database;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.mrdotxin.propsmart.config.typehandler.LineStringTypeHandler;
import com.mrdotxin.propsmart.config.typehandler.MysqlGeoTypeHandler;
import com.mrdotxin.propsmart.config.typehandler.PointTypeHandler;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus配置类
 * 用于配置MyBatis Plus的相关功能，如分页插件、自定义类型处理器等
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 配置MyBatis Plus拦截器
     * 这里主要配置了分页插件，支持MySQL数据库的分页查询
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建MyBatis Plus拦截器实例
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 注册MySQL几何类型处理器
     * 用于处理MySQL空间几何数据类型与Java对象之间的映射
     */
    @Bean
    public MysqlGeoTypeHandler mysqlGeoTypeHandler() {
        return new MysqlGeoTypeHandler();
    }

}