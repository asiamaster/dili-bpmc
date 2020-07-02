package com.dili.bpmd.api;

import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.bpmd.consts.BpmConsts;
import com.dili.bpmd.consts.OrderState;
import com.dili.bpmd.service.OrdersService;
import com.dili.ss.domain.BaseOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-02 17:14:40.
 */
@RestController
@RequestMapping("/api/orders")
public class OrdersApi {
    @Autowired
    OrdersService ordersService;
    @Autowired
    TaskRpc taskRpc;

    /**
     * 生效订单
     * http://bpmd.diligrp.com:8618/api/orders/valid
     * @param variables 任务变量
     * @return
     */
    @RequestMapping(value="/valid", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<Map<String, Object>> valid(@RequestParam Map<String, Object> variables) {
//        Orders orders = DTOUtils.asInstance(variables, Orders.class);
        ordersService.valid((String)variables.get(BpmConsts.ORDER_CODE_KEY));
        variables.put("orderState", OrderState.Valid.getCode());
        return BaseOutput.success().setData(variables);
    }


}