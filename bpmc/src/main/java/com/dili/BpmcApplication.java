package com.dili;

import com.dili.ss.dto.DTOScan;
import com.dili.ss.retrofitful.annotation.RestfulScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        org.activiti.spring.boot.SecurityAutoConfiguration.class
})
@ComponentScan(basePackages = { "com.dili.ss", "com.dili.bpmc", "com.dili.uap.sdk"})
@RestfulScan({"com.dili.uap.sdk.rpc", "com.dili.bpmc.rpc", "com.dili.bpmc.sdk.rpc"})
@MapperScan(basePackages = {"com.dili.bpmc.dao", "com.dili.ss.dao"})
@DTOScan(value={"com.dili.ss", "com.dili.uap", "com.dili.bpmc"})
public class BpmcApplication {

//    @LoadBalanced
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    public static void main(String[] args) {
        SpringApplication.run(BpmcApplication.class, args);
    }

}

