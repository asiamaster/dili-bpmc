package com.dili.bpmd.service.impl;

import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.FormRpc;
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
import com.dili.ss.exception.ParamErrorException;
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
    @Autowired
    FormRpc formRpc;

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
        //启动流程，因为要获取流程实例id,所以先启动流程，如果插入订单失败，无法回滚流程。
        BaseOutput<ProcessInstanceMapping> processInstanceOutput = runtimeRpc.startProcessInstanceByKey(BpmConsts.PROCESS_DEFINITION_KEY, orders.getCode(), userTicket.getId().toString(), variables);
        if(!processInstanceOutput.isSuccess()){
            return BaseOutput.failure(processInstanceOutput.getMessage());
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
    public String handle(String code) throws BusinessException {
        //根据业务号查询任务
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceBusinessKey(code);
        BaseOutput<List<TaskMapping>> output = taskRpc.list(taskDto);
        if(!output.isSuccess()){
            throw new BusinessException(ResultCode.DATA_ERROR, output.getMessage());
        }
        List<TaskMapping> taskMappings = output.getData();
        //没有进行中的任务或任务已结束
        if(CollectionUtils.isEmpty(taskMappings)){
            throw new BusinessException(ResultCode.DATA_ERROR, "未找到进行中的任务");
        }
        //默认没有并发流程，所以取第一个任务
        //如果有并发流程，需使用TaskDefKey来确认流程节点
        TaskMapping taskMapping = taskMappings.get(0);
        String formKey = taskMapping.getFormKey();
        BaseOutput<ActForm> actFormOutput = formRpc.getByKey(formKey);
        //没有已注册的表单配置
        if(!actFormOutput.isSuccess()){
            throw new BusinessException(ResultCode.DATA_ERROR, output.getMessage());
        }
        ActForm actForm = actFormOutput.getData();
        if(actForm == null){
            throw new ParamErrorException("任务表单["+formKey+"]不存在");
        }
        //自动签收任务
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if(userTicket == null){
            throw new NotLoginException();
        }
        BaseOutput baseOutput = taskRpc.claim(taskMapping.getId(), userTicket.getId().toString());
        if(!baseOutput.isSuccess()){
            throw new ParamErrorException(baseOutput.getMessage());
        }
        return new StringBuilder().append("redirect:").append(actForm.getTaskUrl()).append("?cover=false&taskId=").append(taskMapping.getId()).append("&businessKey=").append(code).toString();
    }

    @Override
    public BaseOutput<String> validateBusinessKey(String businessKey){
        //根据业务号查询任务
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceBusinessKey(businessKey);
        BaseOutput<List<TaskMapping>> output = taskRpc.list(taskDto);
        if(!output.isSuccess()){
            return BaseOutput.failure(output.getMessage());
        }
        List<TaskMapping> taskMappings = output.getData();
        //没有进行中的任务或任务已结束
        if(CollectionUtils.isEmpty(taskMappings)){
            return BaseOutput.failure("未找到进行中的任务");
        }
        //默认没有并发流程，所以取第一个任务
        //如果有并发流程，需使用TaskDefKey来确认流程节点
        TaskMapping taskMapping = taskMappings.get(0);
        String formKey = taskMapping.getFormKey();
        BaseOutput<ActForm> actFormOutput = formRpc.getByKey(formKey);
        //没有已注册的表单配置
        if(!actFormOutput.isSuccess()){
            return BaseOutput.failure(output.getMessage());
        }
        ActForm actForm = actFormOutput.getData();
        if(actForm == null){
            return BaseOutput.failure("任务表单["+formKey+"]不存在");
        }
        //自动签收任务
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if(userTicket == null){
            return BaseOutput.failure("用户未登录");
        }
        return taskRpc.claim(taskMapping.getId(), userTicket.getId().toString());
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