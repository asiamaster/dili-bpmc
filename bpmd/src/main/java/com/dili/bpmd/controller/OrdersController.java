package com.dili.bpmd.controller;

import com.dili.bpmc.sdk.annotation.BpmTask;
import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.FormRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.bpmd.domain.Orders;
import com.dili.bpmd.service.OrdersService;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.ss.exception.BusinessException;
import com.dili.ss.exception.ParamErrorException;
import com.dili.ss.util.SpringUtil;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.exception.NotLoginException;
import com.dili.uap.sdk.session.SessionContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-02 17:14:40.
 */
@Controller
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    OrdersService ordersService;
    @Autowired
    TaskRpc taskRpc;
    @Autowired
    FormRpc formRpc;
    /**
     * 订单查询页面
     * @param modelMap
     * @return
     */
    @RequestMapping(value="/index.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(ModelMap modelMap) {
        return "orders/index";
    }

    /**
     * 动态处理订单
     * @param modelMap
     * @return
     */
    @RequestMapping(value="/handle.html", method = RequestMethod.GET)
    public String handle(@RequestParam String code, ModelMap modelMap) throws BusinessException {
        return ordersService.handle(code);
    }

    /**
     * 根据业务号验证任务，用于在插手处理任务前的验证，并签收任务
     * 判断任务是否被签收
     * 同时要验证任务是否存在, 是否有已注册的表单，并且当前用户已登录
     * @param businessKey  订单号
     * @param modelMap
     * @return
     */
    @RequestMapping(value="/validateBusinessKey.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> validateBusinessKey(@RequestParam String businessKey, ModelMap modelMap) {
        return ordersService.validateBusinessKey(businessKey);
    }
    /**
     * 创建订单页面
     * @param modelMap
     * @return
     */
    @RequestMapping(value="/create.html", method = RequestMethod.GET)
    public String create(ModelMap modelMap) {
        return "orders/create";
    }

    /**
     * 创建订单
     * @param orders
     * @return
     */
    @RequestMapping(value="/create.action", method = RequestMethod.POST)
    public String doCreate(Orders orders) {
        ordersService.create(orders);
        //重定向全路径是为了显示面包屑
        return "redirect:"+ SpringUtil.getProperty("bpmn.server.address") + "/orders/index.html";
//        return "orders/index";
    }

    /**
     * 删除订单
     * @param id
     * @return
     */
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> delete(@RequestParam Long id) {
        try {
            return ordersService.logicDelete(id);
//            这里必须手动捕获远程调用主动抛出的异常，因为远程调用本身可能直接抛出异常
        } catch (Exception e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @RequestMapping(value="/cancel.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> cancel(@RequestParam Long id) {
        try {
            return ordersService.cancel(id);
        } catch (BusinessException e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 作废订单
     * @param id
     * @return
     */
    @RequestMapping(value="/invalidate.action", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> invalidate(@RequestParam Long id) {
        try {
            return ordersService.invalidate(id);
        } catch (BusinessException e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 提交订单页面
     * @param modelMap
     * @param cover 用于是否
     * @return
     */
    @BpmTask(formKey = "submitRentalOrderForm", defKey = "submitRentalOrder")
    @RequestMapping(value="/submit.html", method = RequestMethod.GET)
    public String submit(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
//        Map<String, Object> executionVariables = runtimeService.getVariables(task.getExecutionId());
        String code = taskVariablesOutput.getData().get("orderCode").toString();
        modelMap.put("orders", ordersService.getByCode(code));
        modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
        return "orders/submit";
    }

    /**
     * 提交订单
     * @param code  订单号
     * @param taskId 任务id
     * @return
     */
    @RequestMapping(value="/submit.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> doSubmit(@RequestParam String code, @RequestParam String taskId) {
        return ordersService.submit(code, taskId);
    }

    /**
     * 结算订单页面
     * @param modelMap
     * @return
     */
    @BpmTask(formKey = "settlementRentalOrderForm", defKey = "settlementRentalOrder")
    @RequestMapping(value="/settle.html", method = RequestMethod.GET)
    public String settle(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String code = taskVariablesOutput.getData().get("orderCode").toString();
        modelMap.put("orders", ordersService.getByCode(code));
        modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
        return "orders/settle";
    }

    /**
     * 结算订单
     * @param code  订单号
     * @param effectiveTime 生效时间
     * @param deadTime 失败时间
     * @param taskId 任务id
     * @return
     */
    @RequestMapping(value="/settle.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> doSettle(@RequestParam String code, @RequestParam Date effectiveTime, @RequestParam Date deadTime, @RequestParam String taskId) {
        return ordersService.settle(code, effectiveTime, deadTime, taskId);
    }

    /**
     * 查询Orders，返回列表信息
     * @param orders
     * @return
     */
    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Orders> list(Orders orders) {
        return ordersService.list(orders);
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
     * 修改Orders
     * @param orders
     * @return
     */
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Orders orders) {
        ordersService.updateSelective(orders);
        return BaseOutput.success("修改成功");
    }

}