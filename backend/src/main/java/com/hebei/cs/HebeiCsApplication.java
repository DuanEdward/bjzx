package com.hebei.cs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 北京中兴建机职业技能鉴定中心网站后端应用
 *
 * @author Beijing ZXJJ
 * @since 2024-01-10
 */
@SpringBootApplication
@MapperScan("com.hebei.cs.mapper")
public class HebeiCsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HebeiCsApplication.class, args);
    }

}