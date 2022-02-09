package com.junode.edu.message;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: ma wei long
 * @date:   2020年6月28日 上午10:50:37
*/
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.junode.edu")
@EnableFeignClients("com.junode.edu")
@MapperScan("com.junode.edu.message.mapper")
@Slf4j
public class EduMessageApplication implements DisposableBean {

	private static ConfigurableApplicationContext ctx;
	
	public static void main(String[] args) {
    	ctx = SpringApplication.run(EduMessageApplication.class, args);
    	for (String str : ctx.getEnvironment().getActiveProfiles()) {
            log.info(str);
        }
    	log.info("spring cloud junodeEduMessageApplication started!");
    }
	
    @Override
    public void destroy() throws Exception {
        if (null != ctx && ctx.isRunning()) {
            ctx.close();
        }
    }
}