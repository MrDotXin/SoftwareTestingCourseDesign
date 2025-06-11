package com.mrdotxin.propsmart.config.database.mysql;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

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

        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();

        mybatisConfiguration.setMapUnderscoreToCamelCase(false);
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            log.info("Postgresql JDBC URL: {}", hikariDataSource.getJdbcUrl());
            log.info("Driver Class: {}", hikariDataSource.getDriverClassName());
        }

        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOverflow(true);
        paginationInnerInterceptor.setMaxLimit(500L);
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);
        mybatisSqlSessionFactoryBean.setPlugins(mybatisPlusInterceptor);

        mybatisSqlSessionFactoryBean.setConfiguration(mybatisConfiguration);
        return mybatisSqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
