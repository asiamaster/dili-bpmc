package com.dili.bpmc.controller;

import com.dili.bpmc.domain.TaskAssignment;
import com.dili.bpmc.service.TaskAssignmentService;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api("/taskAssignment")
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
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(TaskAssignment taskAssignment) {
        taskAssignmentService.insertSelective(taskAssignment);
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改TaskAssignment
     * @param taskAssignment
     * @return
     */
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(TaskAssignment taskAssignment) {
        taskAssignmentService.updateSelective(taskAssignment);
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除TaskAssignment
     * @param id
     * @return
     */
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        taskAssignmentService.delete(id);
        return BaseOutput.success("删除成功");
    }
}