package com.dili.bpmc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dili.bpmc.domain.HistoricActivityInstanceQueryDto;
import com.dili.bpmc.domain.HistoricTaskInstanceQueryDto;
import com.dili.bpmc.domain.HistoricVariableInstanceQueryDto;
import com.dili.bpmc.sdk.dto.HistoricProcessInstanceQueryDto;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.ValueProviderUtils;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 流程历史控制器
 * @Author: WangMi
 * @Date: 2019/11/21 9:21
 * @Description:
 */
@Controller
@RequestMapping("/his")
public class HistoryController {

    private final Logger log = LoggerFactory.getLogger(HistoryController.class);
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private FormService formService;
    @Autowired
    private HistoryService historyService;

    /**
     * 历史流程实例表
     * @param request
     * @return
     */
    @RequestMapping(value = "/hisProcInst.html", method = {RequestMethod.GET})
    public String hisProcInst(HttpServletRequest request) {
//        request.setAttribute("total", historyService.createHistoricProcessInstanceQuery().count());
        return "process/hisProcInst";
    }

    /**
     * 历史活动信息
     * 这里记录流程流转过的所有节点，与HI_TASKINST不同的是，taskinst只记录usertask内容
     * @param request
     * @return
     */
    @RequestMapping(value = "/hisActInst.html", method = {RequestMethod.GET})
    public String hisActInst(HttpServletRequest request) {
//        request.setAttribute("total", historyService.createHistoricActivityInstanceQuery().count());
        return "process/hisActInst";
    }

    /**
     * 历史任务实例表
     * @param request
     * @return
     */
    @RequestMapping(value = "/hisTaskInst.html", method = {RequestMethod.GET})
    public String hisTaskInst(HttpServletRequest request) {
//        request.setAttribute("total", historyService.createHistoricTaskInstanceQuery().count());
        return "process/hisTaskInst";
    }

    /**
     * 历史变量
     * @param request
     * @return
     */
    @RequestMapping(value = "/hisVarInst.html", method = {RequestMethod.GET})
    public String hisVarInst(HttpServletRequest request) {
//        request.setAttribute("total", historyService.createHistoricVariableInstanceQuery().count());
        return "process/hisVarInst";
    }

    /**
     * 查询历史任务实例表
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listHisTaskInst.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(HistoricTaskInstanceQueryDto param) throws Exception {
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
        int firstResult = (param.getPage()-1)*param.getRows();
        historicTaskInstanceQuery.taskId(param.getTaskId());
        historicTaskInstanceQuery.processDefinitionId(param.getProcessDefinitionId());
        historicTaskInstanceQuery.processInstanceId(param.getProcessInstanceId());
        historicTaskInstanceQuery.taskAssignee(param.getAssignee());
        List list = historicTaskInstanceQuery.listPage(firstResult, param.getRows());
        Long total = historicTaskInstanceQuery.count();
        return toJSONString(param, list, total);
    }

    /**
     * 查询历史流程实例表
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listHisProcInst.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listHisProcInst(HistoricProcessInstanceQueryDto param) throws Exception {
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        int firstResult = (param.getPage()-1)*param.getRows();
        historicProcessInstanceQuery.processDefinitionId(param.getProcessDefinitionId());
        historicProcessInstanceQuery.processInstanceId(param.getProcessInstanceId());
        List list = historicProcessInstanceQuery.listPage(firstResult, param.getRows());
        long total = historicProcessInstanceQuery.count();
        return toJSONString(param, list, total);
    }

    /**
     * 查询历史活动信息
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listHisActInst.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listHisActInst(HistoricActivityInstanceQueryDto param) throws Exception {
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        int firstResult = (param.getPage()-1)*param.getRows();
        historicActivityInstanceQuery.processDefinitionId(param.getProcessDefinitionId());
        historicActivityInstanceQuery.processInstanceId(param.getProcessInstanceId());
        historicActivityInstanceQuery.activityId(param.getActivityId());
        historicActivityInstanceQuery.activityName(param.getActivityName());
        historicActivityInstanceQuery.taskAssignee(param.getAssignee());
        List list = historicActivityInstanceQuery.listPage(firstResult, param.getRows());
        long total = historicActivityInstanceQuery.count();
        return toJSONString(param, list, total);
    }

    /**
     * 查询历史变量
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listHisVarInst.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listHisVarInst(HistoricVariableInstanceQueryDto param) throws Exception {
        HistoricVariableInstanceQuery historicVariableInstanceQuery = historyService.createHistoricVariableInstanceQuery();
        int firstResult = (param.getPage()-1)*param.getRows();
        if(StringUtils.isNotBlank(param.getProcessInstanceId())) {
            historicVariableInstanceQuery.processInstanceId(param.getProcessInstanceId());
        }
        if(StringUtils.isNotBlank(param.getTaskId())) {
            historicVariableInstanceQuery.taskId(param.getTaskId());
        }
        List list = historicVariableInstanceQuery.listPage(firstResult, param.getRows());
        long total = historicVariableInstanceQuery.count();
        return toJSONString(param, list, total);
    }


    /**
     * 先构建provider，再封装成easyuiPageOutput串
     * @param param
     * @param list
     * @param total
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T extends IBaseDomain> String toJSONString(T param, List list, long total) throws Exception {
        EasyuiPageOutput easyuiPageOutput = new EasyuiPageOutput();
        List results = ValueProviderUtils.buildDataByProvider(param, list);
        easyuiPageOutput.setRows(results);
        easyuiPageOutput.setTotal(total);
        return JSON.toJSONString(easyuiPageOutput, SerializerFeature.IgnoreErrorGetter);
    }

}
