package com.dili.bpmd.service;

import com.dili.bpmd.domain.Orders;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.BusinessException;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-02 17:14:40.
 */
public interface OrdersService extends BaseService<Orders, Long> {

    /**
     * 根据订单号查询
     * @param code
     * @return
     */
    Orders getByCode(String code);
    /**
     * 创建订单
     * @return
     */
    BaseOutput create(Orders orders);

    /**
     * 根据订单号提交订单
     * @param code
     * @return
     */
    BaseOutput submit(String code, String taskId);

    /**
     * 根据订单号结算订单
     * @param code
     * @param effectiveTime 生效时间
     * @param deadTime 失败时间
     * @return
     */
    BaseOutput settle(String code, Date effectiveTime, Date deadTime, String taskId);

    /**
     * 订单生效
     * @param code 订单编号，必填
     * @return
     */
    BaseOutput valid(String code);

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    BaseOutput logicDelete(Long id) throws BusinessException;

    /**
     * 作废
     * @param id
     * @return
     */
    BaseOutput invalidate(Long id) throws BusinessException;

    /**
     * 取消
     * @param id
     * @return
     */
    BaseOutput cancel(Long id) throws BusinessException;

    /**
     * 插手处理流程
     * @param code 订单号
     * @throws BusinessException
     * @return 返回处理页面URL
     */
    String handle(String code) throws BusinessException;

    /**
     * 根据业务号验证任务，用于在插手处理任务前的验证，并签收任务
     * 判断任务是否被签收
     * 同时要验证任务是否存在, 是否有已注册的表单，并且当前用户已登录
     * @param businessKey  订单号
     * @return
     */
    BaseOutput<String> validateBusinessKey(String businessKey);
}