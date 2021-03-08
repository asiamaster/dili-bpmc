package com.dili;

import com.dili.ss.dto.DTOScan;
import com.dili.ss.retrofitful.annotation.RestfulScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        org.activiti.spring.boot.SecurityAutoConfiguration.class
})
@ComponentScan(basePackages = { "com.dili.ss", "com.dili.ss.seata", "com.dili.bpmc", "com.dili.uap.sdk", "com.dili.logger.sdk","com.dili.commons" })
@RestfulScan({"com.dili.uap.sdk.rpc", "com.dili.bpmc.rpc", "com.dili.bpmc.sdk.rpc"})
@MapperScan(basePackages = {"com.dili.bpmc.dao", "com.dili.ss.dao"})
@DTOScan(value={"com.dili.ss", "com.dili.uap.sdk.domain", "com.dili.bpmc"})
public class BpmcApplication {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(BpmcApplication.class, args);
    }

//    @Bean
//    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> myWebServerFactoryCustomizer(){
//        return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
//            @Override
//            public void customize(ConfigurableWebServerFactory factory) {
//                factory.setPort(28617);
//            }
//        };
//    }
}

