package com.dili;

import com.dili.ss.dto.DTOScan;
import com.dili.ss.retrofitful.annotation.RestfulScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@ComponentScan(basePackages = { "com.dili.ss", "com.dili.bpmd", "com.dili.bpmc.sdk", "com.dili.uap.sdk"})
@RestfulScan({"com.dili.uap.sdk.rpc", "com.dili.bpmd.rpc", "com.dili.bpmc.sdk.rpc"})
@MapperScan(basePackages = {"com.dili.bpmd.dao", "com.dili.ss.dao"})
@DTOScan(value={"com.dili.ss", "com.dili.uap", "com.dili.bpmd", "com.dili.bpmc"})
public class BpmdApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpmdApplication.class, args);
    }

}

