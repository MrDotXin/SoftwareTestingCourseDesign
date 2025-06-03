package com.mrdotxin.propsmart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mrdotxin.propsmart.mapper")
public class PropSmartApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropSmartApplication.class, args);
    }

}
