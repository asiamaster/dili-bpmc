package com.dili.bpmc.controller;

import com.dili.bpmc.sdk.domain.ActControl;
import com.dili.bpmc.service.ActControlService;
import com.dili.ss.domain.BaseOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-03-19 17:14:28.
 */
@Controller
@RequestMapping("/actControl")
public class ActControlController {
    @Autowired
    ActControlService actControlService;

    /**
     * 跳转到ActControl页面
     * @param modelMap
     * @return
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "actControl/index";
    }

    /**
     * 分页查询ActControl，返回easyui分页信息
     * @param actControl
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(ActControl actControl) throws Exception {
        return actControlService.listEasyuiPageByExample(actControl, true).toString();
    }

    /**
     * 新增ActControl
     * @param actControl
     * @return
     */
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput insert(ActControl actControl) {
        actControl.setControlId(actControl.getControlId().trim());
        if(StringUtils.isBlank(actControl.getName())){
            actControl.setName(actControl.getControlId());
        }
        actControlService.insertSelective(actControl);
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改ActControl
     * @param actControl
     * @return
     */
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput update(ActControl actControl) {
        actControl.setControlId(actControl.getControlId().trim());
        if(StringUtils.isBlank(actControl.getName())){
            actControl.setName(actControl.getControlId());
        }
        actControlService.updateExactSimple(actControl);
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除ActControl
     * @param id
     * @return
     */
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput delete(Long id) {
        actControlService.delete(id);
        return BaseOutput.success("删除成功");
    }


}