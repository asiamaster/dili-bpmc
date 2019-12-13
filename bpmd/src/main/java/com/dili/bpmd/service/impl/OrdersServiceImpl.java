package com.dili.bpmd.service.impl;

import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.rpc.RuntimeRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.bpmc.sdk.util.DateUtils;
import com.dili.bpmd.consts.BpmConsts;
import com.dili.bpmd.consts.OrderState;
import com.dili.bpmd.dao.OrdersMapper;
import com.dili.bpmd.domain.Orders;
import com.dili.bpmd.service.OrdersService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.ss.exception.BusinessException;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.exception.NotLoginException;
import com.dili.uap.sdk.session.SessionContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-02 17:14:40.
 */
@Service
public class OrdersServiceImpl extends BaseServiceImpl<Orders, Long> implements OrdersService {
    @Autowired
    RuntimeRpc runtimeRpc;
    @Autowired
    TaskRpc taskRpc;

    public OrdersMapper getActualDao() {
        return (OrdersMapper)getDao();
    }

    @Override
    public Orders getByCode(String code) {
        Orders condition = DTOUtils.newInstance(Orders.class);
        condition.setCode(code);
        List<Orders> ordersList = listByExample(condition);
        return CollectionUtils.isEmpty(ordersList) ? null : ordersList.get(0);
    }

    @Override
    @Transactional
    public BaseOutput create(Orders orders) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if (userTicket == null) {
            throw new NotLoginException();
        }
        //流程启动参数设置
        Map<String, Object> variables = new HashMap<>(1);
        variables.put(BpmConsts.ORDER_CODE_KEY, orders.getCode());
        //启动流程
        BaseOutput<ProcessInstanceMapping> processInstanceOutput = runtimeRpc.startProcessInstanceByKey(BpmConsts.PROCESS_DEFINITION_KEY, orders.getCode(), userTicket.getId().toString(), variables);
        if(!processInstanceOutput.isSuccess()){
            throw new AppException(processInstanceOutput.getMessage());
        }
        ProcessInstanceMapping processInstance = processInstanceOutput.getData();
        orders.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        orders.setProcessInstanceId(processInstance.getProcessInstanceId());
        orders.setState(OrderState.Create.getCode());
        //创建订单，记录流程实例id和流程定义id
        insertSelective(orders);
        return BaseOutput.success("新增成功");
    }

    @Override
    @Transactional
    public BaseOutput submit(String code, String taskId) {
        Orders orders = DTOUtils.newInstance(Orders.class);
        orders.setState(OrderState.Submit.getCode());
        updateSelectiveByCode(code, orders);
        return taskRpc.complete(taskId);
    }

    @Override
    @Transactional
    public BaseOutput settle(String code, Date effectiveTime, Date deadTime, String taskId) {
        Orders orders = DTOUtils.newInstance(Orders.class);
        orders.setState(OrderState.Payed.getCode());
        orders.setEffectiveTime(effectiveTime);
        orders.setDeadTime(deadTime);
        updateSelectiveByCode(code, orders);
        Map<String, Object> variables = new HashMap<>(2);
        variables.put(BpmConsts.ORDER_CODE_KEY, code);
        //设置ISO8601格式的订单生效时间，用于流程中的定时器
        variables.put(BpmConsts.FIRE_TIME, DateUtils.getISO8601TimeDate(effectiveTime));
        return taskRpc.complete(taskId, variables);
    }

    @Override
    @Transactional
    public BaseOutput valid(String code) {
        Orders orders = DTOUtils.newInstance(Orders.class);
        orders.setState(OrderState.Valid.getCode());
        updateSelectiveByCode(code, orders);
        return BaseOutput.success();
    }

    @Override
    @Transactional
    public BaseOutput logicDelete(Long id) {
        Orders orders = DTOUtils.newInstance(Orders.class);
        orders.setYn(false);
        orders.setId(id);
        updateSelective(orders);
        orders = get(id);
        //发送消息通知流程
        return taskRpc.messageEventReceived("deleteRentalOrderMsg", orders.getProcessInstanceId(), null);
        //
//        if(!output.isSuccess()){
//            throw new BusinessException(ResultCode.DATA_ERROR, output.getMessage());
//        }
    }

    @Override
    @Transactional
    public BaseOutput invalidate(Long id) {
        Orders orders = DTOUtils.newInstance(Orders.class);
        orders.setState(OrderState.Invalid.getCode());
        orders.setId(id);
        updateSelective(orders);
        orders = get(id);
        //发送消息通知流程
        return taskRpc.messageEventReceived("invalidRentalOrderMsg", orders.getProcessInstanceId(), null);
//        if(!output.isSuccess()){
//            throw new BusinessException(ResultCode.DATA_ERROR, output.getMessage());
//        }
    }

    @Override
    @Transactional
    public BaseOutput cancel(Long id) {
        Orders orders = DTOUtils.newInstance(Orders.class);
        orders.setState(OrderState.Cancel.getCode());
        orders.setId(id);
        updateSelective(orders);
        orders = get(id);
        //发送消息通知流程
        return taskRpc.messageEventReceived("deleteRentalOrderMsg", orders.getProcessInstanceId(), null);
//        if(!output.isSuccess()){
//            throw new BusinessException(ResultCode.DATA_ERROR, output.getMessage());
//        }
    }

    @Override
    @Transactional
    public BaseOutput handle(Long id) throws BusinessException {
        Orders orders = get(id);
        //发送消息通知流程
        return taskRpc.messageEventReceived("deleteRentalOrderMsg", orders.getProcessInstanceId(), null);
//        if(!output.isSuccess()){
//            throw new BusinessException(ResultCode.DATA_ERROR, output.getMessage());
//        }
    }

    /**
     * 根据编号修改订单
     * @param code
     * @param orders
     * @return
     */
    private int updateSelectiveByCode(String code, Orders orders){
        Orders condition = DTOUtils.newInstance(Orders.class);
        condition.setCode(code);
        return updateSelectiveByExample(orders, condition);
    }

}