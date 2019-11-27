package com.dili;

import com.dili.ss.dto.DTOScan;
import com.dili.ss.retrofitful.annotation.RestfulScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        org.activiti.spring.boot.SecurityAutoConfiguration.class
})
@ComponentScan(basePackages = { "com.dili.ss", "com.dili.bpmc", "com.dili.uap.sdk"})
@RestfulScan({"com.dili.ss.activiti.rpc", "com.dili.uap.sdk.rpc", "com.dili.bpmc.rpc"})
@MapperScan(basePackages = {"com.dili.uap.dao", "com.dili.ss.activiti.dao", "com.dili.ss.dao"})
@DTOScan(value={"com.dili.ss", "com.dili.uap", "com.dili.bpmc"})
public class BpmcApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpmcApplication.class, args);
    }

}

