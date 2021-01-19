package com.dili.bpmc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dili.bpmc.sdk.domain.ModelMapping;
import com.dili.logger.sdk.annotation.BusinessLogger;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.ss.activiti.service.ActivitiService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 流程模型控制器
 * @author wm
 * @date 2019-3-6
 * @since 1.0
 */
@Controller
@RequestMapping("/model")
public class ModelController {

    private final Logger log = LoggerFactory.getLogger(ModelController.class);

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ActivitiService activitiService;


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
        List<Model> list = repositoryService.createModelQuery().listPage(rowSize * (page - 1), rowSize);
        EasyuiPageOutput easyuiPageOutput = new EasyuiPageOutput();
        easyuiPageOutput.setTotal(new Long(list.size()));
        easyuiPageOutput.setRows(list);
        request.setAttribute("models", JSON.toJSONString(easyuiPageOutput, SerializerFeature.WriteDateUseDateFormat));
        return "process/model";
    }

    /**
     * 删除模型
     * @param modelId 模型id
     * @return
     */
    @BusinessLogger(businessType = "bpmc", content = "删除流程", operationType = "del", systemCode = "BPMC")
    @RequestMapping(value = "/del.action", method = {RequestMethod.GET})
    public void deleteModel(@RequestParam String modelId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        activitiService.deleteModel(modelId);
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, modelId);
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, modelId);
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        if (userTicket != null) {
            LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, userTicket.getId());
            LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, userTicket.getRealName());
            LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, userTicket.getFirmId());
        }
        response.sendRedirect(request.getContextPath() + "/model/index.html");
    }

    /**
     * 查询
     * @param model
     * @return
     */
    @RequestMapping(value = "/list.action",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String list(ModelMapping model) throws Exception {
        ModelQuery modelQuery = repositoryService.createModelQuery();
        if(StringUtils.isNotBlank(model.getCategory())){
            modelQuery.modelCategory(model.getCategory());
        }
        if(StringUtils.isNotBlank(model.getKey())){
            modelQuery.modelKey(model.getKey());
        }
        if(StringUtils.isNotBlank(model.getName())){
            modelQuery.modelNameLike("%"+model.getName()+"%");
        }
        List list = modelQuery.list();

        List results = ValueProviderUtils.buildDataByProvider(model.getMetadata(), list);
        EasyuiPageOutput easyuiPageOutput = new EasyuiPageOutput((long)results.size(), results);
        return easyuiPageOutput.toString();
    }

    /**
     * 修改
     * @param modelParam
     * @return
     */
    @BusinessLogger(businessType = "bpmc", content = "修改流程", operationType = "edit", systemCode = "BPMC")
    @RequestMapping(value = "/update.action",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public BaseOutput<String> update(Model modelParam) {
        Model model = repositoryService.getModel(modelParam.getId());
        model.setName(modelParam.getName());
        model.setCategory(modelParam.getCategory());
        model.setKey(modelParam.getKey());
        Map<String, String> metaInfo = (Map<String, String>) JSON.parse(model.getMetaInfo());
        metaInfo.put("name",modelParam.getName());
        model.setMetaInfo(JSON.toJSONString(metaInfo));
        try {
            repositoryService.saveModel(model);
            LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, modelParam.getName());
            LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, modelParam.getId());
            UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
            if (userTicket != null) {
                LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, userTicket.getId());
                LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, userTicket.getRealName());
                LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, userTicket.getFirmId());
            }
        } catch (Exception e) {
            log.error("id为"+modelParam.getId()+"修改模型设计失败:"+e.getMessage());
            return BaseOutput.failure(e.getMessage());
        }
        return BaseOutput.success();
    }

    /**
     * 根据模型id获取xml
     * @param modelId
     * @return
     */
    @GetMapping(value = "/xml.action")
    @ResponseBody
    public BaseOutput<String> getXml(@RequestParam("modelId") String modelId) {
        try {
            Model modelData = repositoryService.getModel(modelId);
            byte[] modelEditorSource = repositoryService.getModelEditorSource(modelData.getId());

            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree((new String(modelEditorSource,"UTF-8")).getBytes("UTF-8"));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);

            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] exportBytes = xmlConverter.convertToXML(bpmnModel,"UTF-8");
            return BaseOutput.successData(new String(exportBytes,"UTF-8"));
        }catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 保存xml模型
     * @param id
     * @param xml
     * @param name
     * @param description
     * @return
     */
    @PostMapping(value="/saveModel.action")
    @ResponseBody
    public BaseOutput saveModel(@RequestParam String id,
                                @RequestParam String name,
                                @RequestParam(required = false) String key,
                                @RequestParam(required = false) String category,
                                @RequestParam(required = false) String description,
                                @RequestParam String xml) {
        try{
            String unescapeXml = StringEscapeUtils.unescapeHtml(xml);//因过滤处理XSS时会对<,>等字符转码，此处需将字符串还原
            InputStream in_nocode   =   new ByteArrayInputStream(unescapeXml.getBytes("UTF-8"));
            XMLInputFactory xmlFactory  = XMLInputFactory.newInstance();
            XMLStreamReader reader = xmlFactory.createXMLStreamReader(in_nocode);

            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            BpmnModel bpmnModel = xmlConverter.convertToBpmnModel(reader);

            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode j =jsonConverter.convertToJson(bpmnModel);

            ObjectMapper objectMapper = new ObjectMapper();
            byte[] modelEditorSource = objectMapper.writeValueAsBytes(j);

            MultiValueMap<String,String> values = new LinkedMultiValueMap<String,String>();
            values.add("json_xml", new String(modelEditorSource,"UTF-8"));
            values.add("svg_xml", "");
            values.add("name", name);
            values.add("description", description);

            Model model = repositoryService.getModel(id);
            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            modelJson.put("name", values.getFirst("name"));
            modelJson.put("description", values.getFirst("description"));
            model.setMetaInfo(modelJson.toString());
            model.setName(name);
            model.setCategory(category);
            model.setKey(key);
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), values.getFirst("json_xml").getBytes("utf-8"));
            return BaseOutput.success();
        }catch(Exception e){
            e.printStackTrace();
            return BaseOutput.failure("更新模型XML失败:"+e.getMessage());
        }
    }

}
