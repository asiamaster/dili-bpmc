package com.dili.bpmc.provider;

import com.alibaba.fastjson.JSONObject;
import com.dili.bpmc.domain.DataDictionaryValue;
import com.dili.bpmc.rpc.ActDataDictionaryRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.provider.BatchDisplayTextProviderAdaptor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远程数据字典批量提供者
 */
@Component("actDataDictionaryValueProvider")
@Scope("prototype")
public class ActDataDictionaryValueProvider extends BatchDisplayTextProviderAdaptor {

    //前台需要传入的参数
    protected static final String DD_CODE_KEY = "dd_code";
    @Autowired
    private ActDataDictionaryRpc dataDictionaryRpc;

    @Override
    public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
        Object queryParams = metaMap.get(QUERY_PARAMS_KEY);
        if(queryParams == null) {
            return Lists.newArrayList();
        }

        String ddCode = getDdCode(queryParams.toString());
        DataDictionaryValue dataDictionaryValue = DTOUtils.newDTO(DataDictionaryValue.class);
        dataDictionaryValue.setDdCode(ddCode);
        BaseOutput<List<DataDictionaryValue>> output = dataDictionaryRpc.list(dataDictionaryValue);
        if(!output.isSuccess()){
            return null;
        }
        List<ValuePair<?>> valuePairs = Lists.newArrayList();
        List<DataDictionaryValue> dataDictionaryValues = output.getData();
        for(int i=0; i<dataDictionaryValues.size(); i++) {
            DataDictionaryValue dataDictionaryValue1 = dataDictionaryValues.get(i);
            valuePairs.add(i, new ValuePairImpl(dataDictionaryValue1.getName(), dataDictionaryValue1.getCode()));
        }
        return valuePairs;
    }

    @Override
    protected List getFkList(List<String> ddvIds, Map metaMap) {
        Object queryParams = metaMap.get(QUERY_PARAMS_KEY);
        if(queryParams == null) {
            return Lists.newArrayList();
        }
        String ddCode = getDdCode(queryParams.toString());
        DataDictionaryValue dataDictionaryValue = DTOUtils.newDTO(DataDictionaryValue.class);
        dataDictionaryValue.setDdCode(ddCode);
        BaseOutput<List<DataDictionaryValue>> output = dataDictionaryRpc.list(dataDictionaryValue);
        return output.isSuccess() ? output.getData() : null;
    }

    @Override
    protected Map<String, String> getEscapeFileds(Map metaMap) {
        if(metaMap.get(ESCAPE_FILEDS_KEY) instanceof Map){
            return (Map)metaMap.get(ESCAPE_FILEDS_KEY);
        }else {
            Map<String, String> map = new HashMap<>();
            map.put(metaMap.get(FIELD_KEY).toString(), "name");
            return map;
        }
    }

    /**
     * 关联(数据库)表的主键的字段名
     * 默认取id，子类可自行实现
     * @return
     */
    @Override
    protected String getRelationTablePkField(Map metaMap) {
        return "code";
    }

    /**
     * 获取数据字典编码
     * @return
     */
    public String getDdCode(String queryParams){
        //清空缓存
        String ddCode = JSONObject.parseObject(queryParams).getString(DD_CODE_KEY);
        if(ddCode == null){
            throw new RuntimeException("dd_code属性为空");
        }
        return ddCode;
    }

    @Override
    protected boolean ignoreCaseToRef(Map metaMap){
        return true;
    }
}