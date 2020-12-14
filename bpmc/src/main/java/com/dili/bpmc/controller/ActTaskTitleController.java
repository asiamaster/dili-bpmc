package com.dili.bpmc.controller;

import com.dili.bpmc.domain.ActTaskTitle;
import com.dili.bpmc.service.ActTaskTitleService;
import com.dili.logger.sdk.annotation.BusinessLogger;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-10-27 09:40:40.
 */
@Controller
@RequestMapping("/actTaskTitle")
public class ActTaskTitleController {
    @Autowired
    ActTaskTitleService actTaskTitleService;

    /**
     * 跳转到ActTaskTitle页面
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "actTaskTitle/index";
    }

    /**
     * 分页查询ActTaskTitle，返回easyui分页信息
     * @param actTaskTitle
     * @return String
     * @throws Exception
     */
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(ActTaskTitle actTaskTitle) throws Exception {
        return actTaskTitleService.listEasyuiPageByExample(actTaskTitle, true).toString();
    }

    /**
     * 新增ActTaskTitle
     * @param actTaskTitle
     * @return BaseOutput
     */
    @BusinessLogger(businessType = "bpmc", content = "新增ActTaskTitle", operationType = "add", systemCode = "BPMC")
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(ActTaskTitle actTaskTitle) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if (userTicket == null) {
            return BaseOutput.failure("登录超时");
        }
        actTaskTitle.setCreaterId(userTicket.getId());
        actTaskTitle.setModifierId(userTicket.getId());
        actTaskTitleService.insertSelective(actTaskTitle);
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, actTaskTitle.getProcessDefinitionId());
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, actTaskTitle.getId());

        LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, userTicket.getId());
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, userTicket.getRealName());
        LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, userTicket.getFirmId());
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改ActTaskTitle
     * @param actTaskTitle
     * @return BaseOutput
     */
    @BusinessLogger(businessType = "bpmc", content = "修改ActTaskTitle", operationType = "edit", systemCode = "BPMC")
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(ActTaskTitle actTaskTitle) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if (userTicket == null) {
            return BaseOutput.failure("登录超时");
        }
        actTaskTitle.setModifierId(userTicket.getId());
        actTaskTitleService.updateExactSimple(actTaskTitle);
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, actTaskTitle.getProcessDefinitionId());
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, actTaskTitle.getId());

        LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, userTicket.getId());
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, userTicket.getRealName());
        LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, userTicket.getFirmId());
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除ActTaskTitle
     * @param id
     * @return BaseOutput
     */
    @BusinessLogger(businessType = "bpmc", content = "删除ActTaskTitle", operationType = "del", systemCode = "BPMC")
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        actTaskTitleService.delete(id);
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