package com.lagou.edu.gateway;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients(basePackages = "com.lagou.edu")
//@EnableDiscoveryClient
//@EnableCircuitBreaker
@EnableMethodCache(basePackages = "com.lagou.edu")
@EnableCreateCacheAnnotation
public class LagouGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(LagouGatewayApplication.class, args);
    }
}
