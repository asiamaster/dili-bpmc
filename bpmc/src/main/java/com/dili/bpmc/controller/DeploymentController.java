package com.dili.bpmc.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dili.ss.activiti.service.ActivitiService;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.BusinessException;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.apache.commons.io.IOUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 流程部署控制器
 */
@Controller
@RequestMapping("/deployment")
public class DeploymentController {
    //模型管理首页地址
    private final String INDEX = "/model/index.html";
    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;

    private final Logger log = LoggerFactory.getLogger(DeploymentController.class);

    /**
     * 模型管理首页
     * @param modelMap
     * @param request
     * @return
     */
    @RequestMapping(value = "/index.html", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(ModelMap modelMap, HttpServletRequest request) {
        int rowSize = 20;
        int page = 1;
        List<Deployment> deployments = repositoryService.createDeploymentQuery()
                .listPage(rowSize * (page - 1), rowSize);
        request.setAttribute("deployments", JSONArray.toJSONString(deployments, SerializerFeature.WriteDateUseDateFormat , SerializerFeature.IgnoreErrorGetter));
        return "process/deploy";
    }
    /**
     * 显示部署流程图片(无节点高亮)
     * @param deploymentId
     * @param response
     * @throws Exception
     */
    @GetMapping(value = "/img.action")
    public void showImageByDeploymentId(@RequestParam String deploymentId, HttpServletResponse response) throws Exception{
        activitiService.showImageByDeploymentId(deploymentId, response);
//        List<String> list = repositoryService.getDeploymentResourceNames(deploymentId);
//        // 定义图片资源的名称
//        String resourceName = "";
//        if (list != null && list.size() > 0) {
//            for (String name : list) {
//                if (name.indexOf(".png") >= 0 || name.indexOf(".jpg") >= 0) {
//                    resourceName = name;
//                    break;
//                }
//            }
//        }
//        InputStream inputStream = repositoryService.getResourceAsStream(deploymentId,resourceName);
//        int b = -1;
//        OutputStream outputStream = response.getOutputStream();
//        while ((b=inputStream.read())!=-1){
//            outputStream.write(b);
//        }
//        inputStream.close();
//        outputStream.close();
    }

    /**
     * 删除流程部署
     * @param deploymentId
     * @return
     */
    @GetMapping(value = "/delete.action")
    public void deleteDeployment(@RequestParam String deploymentId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        activitiService.deleteDeployment(deploymentId);
        response.sendRedirect(request.getContextPath() + INDEX);
    }

    /**
     * 发布模型为流程定义
     * @param modelId
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/deploy.action")
    public void deploy(@RequestParam("modelId")String modelId, HttpServletRequest request, HttpServletResponse response) throws IOException, BusinessException {
        try {
            activitiService.deployByModelId(modelId);
        }catch (ActivitiException e){
            log.warn(e.getMessage());
            throw new BusinessException(ResultCode.DATA_ERROR, e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + INDEX);
    }

    /**
     * 根据modelId部署流程，测试用代码
     * @param modelId
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/deployForm.action")
    @ResponseBody
    public BaseOutput<String> deployForm(@RequestParam("modelId")String modelId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Deployment deployment = activitiService.deployFormByModelId(modelId);
        InputStream is = repositoryService.getResourceAsStream(deployment.getId(), "start.form");
        System.out.println(new String(IOUtils.toByteArray(is), "utf-8"));
        return BaseOutput.success();
    }

    /**
     * 测试代码，可在部署流程和表单后，获取外置表单信息
     * @param processDefinitionId
     * @param response
     * @throws IOException
     */
    @GetMapping(value = "/getRenderedStartForm.html")
    public void getRenderedStartForm(@RequestParam("processDefinitionId")String processDefinitionId, HttpServletResponse response) throws IOException {
        Object template = formService.getRenderedStartForm(processDefinitionId);
        StringTemplateResourceLoader resourceLoader = new
                StringTemplateResourceLoader();
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        Template t = gt.getTemplate("<html lang=\"en\"><head><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" /><meta charset=\"utf-8\" /><title>activiti</title></head><body>"+template.toString() + "hello,${name} <body></html>");
        t.binding("name", "beetl");
        String str = t.render();
        response.getWriter().write(str);
    }

}