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
     * @param code
     * @return
     */
    BaseOutput valid(String code);

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    void logicDelete(Long id) throws BusinessException;

    /**
     * 作废
     * @param id
     * @return
     */
    void invalidate(Long id) throws BusinessException;

    /**
     * 取消
     * @param id
     * @return
     */
    void cancel(Long id) throws BusinessException;

    /**
     * 插手处理流程
     * @param id
     * @throws BusinessException
     */
    void handle(Long id) throws BusinessException;
}