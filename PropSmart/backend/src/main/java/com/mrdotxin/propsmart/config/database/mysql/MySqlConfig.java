package com.mrdotxin.propsmart.config.database.mysql;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Slf4j
@Configuration
@MapperScan(
        basePackages = "com.mrdotxin.propsmart.mapper.mysql",
        sqlSessionFactoryRef = "mysqlSqlSessionFactory"
)
public class MySqlConfig {

    @Resource
    private MysqlDataSourceProperties properties;

    @Primary
    @Bean(name="mysqlDataSource")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create()
            .url(properties.getUrl())
            .driverClassName(properties.getDriverClassName())
            .username(properties.getUsername())
            .password(properties.getPassword())
            .type(com.zaxxer.hikari.HikariDataSource.class)
            .build();
    }

    @Primary
    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSource);

        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            log.info("Postgresql JDBC URL: {}", hikariDataSource.getJdbcUrl());
            log.info("Driver Class: {}", hikariDataSource.getDriverClassName());
        }

        return mybatisSqlSessionFactoryBean.getObject();
    }
}
