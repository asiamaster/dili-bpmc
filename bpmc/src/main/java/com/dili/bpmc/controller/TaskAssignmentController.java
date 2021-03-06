package com.dili.bpmc.controller;

import com.dili.bpmc.domain.TaskAssignment;
import com.dili.bpmc.service.TaskAssignmentService;
import com.dili.logger.sdk.annotation.BusinessLogger;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-25 14:11:30.
 */
@Controller
@RequestMapping("/taskAssignment")
public class TaskAssignmentController {
    @Autowired
    TaskAssignmentService taskAssignmentService;

    /**
     * 跳转到TaskAssignment页面
     * @param modelMap
     * @return
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "taskAssignment/index";
    }

    /**
     * 查询TaskAssignment，返回列表信息
     * @param taskAssignment
     * @return
     */
    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskAssignment> list(TaskAssignment taskAssignment) {
        return taskAssignmentService.list(taskAssignment);
    }

    /**
     * 分页查询TaskAssignment，返回easyui分页信息
     * @param taskAssignment
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(TaskAssignment taskAssignment) throws Exception {
        return taskAssignmentService.listEasyuiPageByExample(taskAssignment, true).toString();
    }

    /**
     * 新增TaskAssignment
     * @param taskAssignment
     * @return
     */
    @BusinessLogger(businessType = "bpmc", content = "新增TaskAssignment", operationType = "add", systemCode = "BPMC")
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(TaskAssignment taskAssignment) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if(userTicket == null){
            return BaseOutput.failure("登录超时");
        }
        taskAssignment.setCreaterId(userTicket.getId());
        taskAssignment.setModifierId(userTicket.getId());
        taskAssignmentService.insertSelective(taskAssignment);
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, taskAssignment.getTaskDefinitionKey());
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, taskAssignment.getId());

        LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, userTicket.getId());
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, userTicket.getRealName());
        LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, userTicket.getFirmId());
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改TaskAssignment
     * @param taskAssignment
     * @return
     */
    @BusinessLogger(businessType = "bpmc", content = "修改TaskAssignment", operationType = "edit", systemCode = "BPMC")
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(TaskAssignment taskAssignment) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if(userTicket == null){
            return BaseOutput.failure("登录超时");
        }
        taskAssignment.setModifierId(userTicket.getId());
        taskAssignmentService.updateExactSimple(taskAssignment);

        LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, taskAssignment.getTaskDefinitionKey());
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, taskAssignment.getId());

        LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, userTicket.getId());
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, userTicket.getRealName());
        LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, userTicket.getFirmId());
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除TaskAssignment
     * @param id
     * @return
     */
    @BusinessLogger(businessType = "bpmc", content = "删除TaskAssignment", operationType = "del", systemCode = "BPMC")
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        taskAssignmentService.delete(id);
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, id);
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, id);
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if (userTicket != null) {
            LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, userTicket.getId());
            LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, userTicket.getRealName());
            LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, userTicket.getFirmId());
        }
        return BaseOutput.success("删除成功");
    }
}