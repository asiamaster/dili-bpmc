package com.dili.bpmd.controller;

import com.dili.bpmc.sdk.rpc.EventRpc;
import com.dili.bpmc.sdk.rpc.FormRpc;
import com.dili.bpmc.sdk.rpc.HistoryRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.bpmd.cache.BpmdCache;
import com.dili.bpmd.domain.Orders;
import com.dili.bpmd.service.OrdersService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 动态流转订单显示
 * @author wangmi
 */
@Controller
@RequestMapping("/dynamicProcessOrders")
public class DynamicProcessOrdersController {
    @Autowired
    OrdersService ordersService;
    @SuppressWarnings("all")
    @Autowired
    TaskRpc taskRpc;
    @SuppressWarnings("all")
    @Autowired
    FormRpc formRpc;
    @SuppressWarnings("all")
    @Autowired
    HistoryRpc historyRpc;
    @SuppressWarnings("all")
    @Autowired
    EventRpc eventRpc;

    /**
     * 订单查询页面
     * @param modelMap
     * @return
     */
    @GetMapping(value="/index.html")
    public String index(ModelMap modelMap) {
        return "dynamicProcessOrders/index";
    }

    /**
     * 分页查询Orders，返回easyui分页信息
     * @param orders
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Orders orders) throws Exception {
        orders.setYn(true);
        return ordersService.listEasyuiPageByExample(orders, true).toString();
    }

    /**
     * 根据流程实例id查询当前事件名称列表
     * @param processInstanceId
     * @param state
     * @return BaseOutput
     */
    @PostMapping(value="/listEventName.action")
    @ResponseBody
    public BaseOutput<List<String>> listEventName(@RequestParam String processInstanceId, @RequestParam Integer state) {
        String cacheKey = processInstanceId + "_" + state;
        if(BpmdCache.ordersEventCache.containsKey(cacheKey)){
            return BaseOutput.successData(BpmdCache.ordersEventCache.get(cacheKey));
        }
        BaseOutput<List<String>> listBaseOutput = eventRpc.listEventName(processInstanceId);
        if(!listBaseOutput.isSuccess()){
            return BaseOutput.failure("调用流控中心远程接口失败:" + listBaseOutput.getMessage());
        }
        BpmdCache.ordersEventCache.put(cacheKey, listBaseOutput.getData());
        return listBaseOutput;
    }

    /**
     * 订单详情页面
     * @param modelMap
     * @return
     */
    @RequestMapping(value="/detail.html", method = RequestMethod.GET)
    public String detail(@RequestParam String businessKey,  ModelMap modelMap) {
        Orders orders = ordersService.getByCode(businessKey);
        modelMap.put("orders", orders);
        return "dynamicProcessOrders/detail";
    }

    /**
     * 创建订单
     * @param orders
     * @return
     */
    @RequestMapping(value="/insert.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput insert(Orders orders) {
        ordersService.createDyna(orders);
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改订单
     * @param orders
     * @return BaseOutput
     */
    @PostMapping(value="/update.action")
    @ResponseBody
    public BaseOutput update(Orders orders) {
        ordersService.updateSelective(orders);
        return BaseOutput.success("修改成功");
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PostMapping(value="/cancel.action")
    @ResponseBody
    public BaseOutput<String> cancel(@RequestParam Long id, @RequestParam String processInstanceId) {
        try {
            return ordersService.cancelDyna(id, processInstanceId);
        } catch (BusinessException e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 提交订单
     * @param code  订单号
     * @param processInstanceId 流程实例id
     * @return
     */
    @PostMapping(value="/submit.action")
    @ResponseBody
    public BaseOutput submit(@RequestParam String code, @RequestParam String processInstanceId) {
        return ordersService.submitDyna(code, processInstanceId);
    }

    /**
     * 提交审批
     * @param code  订单号
     * @param processInstanceId 流程实例id
     * @return
     */
    @PostMapping(value="/submitApproval.action")
    @ResponseBody
    public BaseOutput submitApproval(@RequestParam String code, @RequestParam String processInstanceId) {
        return ordersService.submitApproval(code, processInstanceId);
    }

    /**
     * 确认付款订单
     * @param id
     * @return
     */
    @PostMapping(value="/paid.action")
    @ResponseBody
    public BaseOutput<String> paid(@RequestParam Long id, @RequestParam String processInstanceId) {
        try {
            return ordersService.paid(id, processInstanceId);
        } catch (BusinessException e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 补录
     * @param id
     * @return
     */
    @PostMapping(value="/supplement.action")
    @ResponseBody
    public BaseOutput<String> supplement(@RequestParam Long id, @RequestParam String processInstanceId) {
        try {
            return ordersService.supplement(processInstanceId);
        } catch (BusinessException e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 撤回
     * @param id
     * @return
     */
    @PostMapping(value="/withdraw.action")
    @ResponseBody
    public BaseOutput<String> withdraw(@RequestParam Long id, @RequestParam String processInstanceId) {
        try {
            return ordersService.withdraw(id, processInstanceId);
        } catch (BusinessException e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 订单到期
     * @param id
     * @return
     */
    @PostMapping(value="/expired.action")
    @ResponseBody
    public BaseOutput<String> expired(@RequestParam Long id, @RequestParam String processInstanceId) {
        try {
            return ordersService.expired(id, processInstanceId);
        } catch (BusinessException e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

}