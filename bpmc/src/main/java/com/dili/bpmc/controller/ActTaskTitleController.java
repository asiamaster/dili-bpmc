package com.dili.bpmc.controller;

import com.dili.bpmc.domain.ActTaskTitle;
import com.dili.bpmc.service.ActTaskTitleService;
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
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(ActTaskTitle actTaskTitle) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if (userTicket == null) {
            return BaseOutput.failure("登录超时");
        }
        actTaskTitle.setCreaterId(userTicket.getId());
        actTaskTitle.setModifierId(userTicket.getId());
        actTaskTitleService.insertSelective(actTaskTitle);
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改ActTaskTitle
     * @param actTaskTitle
     * @return BaseOutput
     */
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(ActTaskTitle actTaskTitle) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if (userTicket == null) {
            return BaseOutput.failure("登录超时");
        }
        actTaskTitle.setModifierId(userTicket.getId());
        actTaskTitleService.updateExactSimple(actTaskTitle);
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除ActTaskTitle
     * @param id
     * @return BaseOutput
     */
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        actTaskTitleService.delete(id);
        return BaseOutput.success("删除成功");
    }
}