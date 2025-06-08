package com.mrdotxin.propsmart.config.database.postgresql;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.mrdotxin.propsmart.config.typehandler.LineStringTypeHandler;
import com.mrdotxin.propsmart.config.typehandler.PointTypeHandler;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Slf4j
@Configuration
@MapperScan(
        basePackages = "com.mrdotxin.propsmart.mapper.postgresql",
        sqlSessionFactoryRef = "postgresqlSqlSessionFactory"
)
public class PostgresqlConfig {

    @Resource
    private PostgresqlDataSourceProperties properties;

    @Bean(name = "postgresqlDataSource")
    public DataSource postgresqlDataSource() {
        return DataSourceBuilder.create()
            .url(properties.getUrl())
            .driverClassName(properties.getDriverClassName())
            .username(properties.getUsername())
            .password(properties.getPassword())
            .type(com.zaxxer.hikari.HikariDataSource.class)
            .build();
    }

    @Bean(name = "postgresqlSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("postgresqlDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.getTypeHandlerRegistry().register(Point.class, PointTypeHandler.class);
        mybatisConfiguration.getTypeHandlerRegistry().register(LineString.class, LineStringTypeHandler.class);

        bean.setConfiguration(mybatisConfiguration);
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            log.info("Postgresql JDBC URL: {}", hikariDataSource.getJdbcUrl());
            log.info("Driver Class: {}", hikariDataSource.getDriverClassName());
        }

        return bean.getObject();
    }
}
