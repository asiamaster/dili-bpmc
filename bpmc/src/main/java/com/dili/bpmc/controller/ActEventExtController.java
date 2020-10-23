package com.dili.bpmc.controller;

import com.dili.bpmc.domain.ActEventExt;
import com.dili.bpmc.service.ActEventExtService;
import com.dili.ss.domain.BaseOutput;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 动态事件扩展
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-10-21 14:36:44.
 */
@Controller
@RequestMapping("/actEventExt")
public class ActEventExtController {
    @Autowired
    ActEventExtService actEventExtService;

    /**
     * 跳转到ActEventExt页面
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "actEventExt/index";
    }

    /**
     * 分页查询ActEventExt，返回easyui分页信息
     * @param actEventExt
     * @return String
     * @throws Exception
     */
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(ActEventExt actEventExt) throws Exception {
        return actEventExtService.listEasyuiPageByExample(actEventExt, true).toString();
    }

    /**
     * 新增ActEventExt
     * @param actEventExt
     * @return BaseOutput
     */
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(ActEventExt actEventExt) {
        actEventExtService.insertSelective(actEventExt);
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改ActEventExt
     * @param actEventExt
     * @return BaseOutput
     */
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(ActEventExt actEventExt) {
        actEventExtService.updateSelective(actEventExt);
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除ActEventExt
     * @param id
     * @return BaseOutput
     */
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        actEventExtService.delete(id);
        return BaseOutput.success("删除成功");
    }
}