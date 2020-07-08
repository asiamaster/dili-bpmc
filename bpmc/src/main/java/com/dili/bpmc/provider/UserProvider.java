package com.dili.bpmc.provider;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.BatchProviderMeta;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.provider.BatchDisplayTextProviderSupport;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.UserRpc;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <B>Description</B>
 * <B>Copyright:本软件源代码版权归农丰时代所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/2/24 18:17
 */
@Component
public class UserProvider extends BatchDisplayTextProviderSupport {

    @SuppressWarnings("all")
    @Autowired
    private UserRpc userRpc;

    @Override
    public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
        return null;
    }


    @Override
    protected List getFkList(List<String> relationIds, Map metaMap) {
        if (CollectionUtils.isEmpty(relationIds)) {
            return Collections.EMPTY_LIST;
        }
        relationIds = relationIds.stream().distinct().collect(Collectors.toList());
        BaseOutput<List<User>> listBaseOutput = userRpc.listUserByIds(relationIds);
        if(!listBaseOutput.isSuccess()){
            return Collections.EMPTY_LIST;
        }
        return listBaseOutput.getData();
    }

    @Override
    protected BatchProviderMeta getBatchProviderMeta(Map metaMap) {
        BatchProviderMeta batchProviderMeta = DTOUtils.newInstance(BatchProviderMeta.class);
        //设置主DTO和关联DTO需要转义的字段名
        batchProviderMeta.setEscapeFiled("realName");
        //忽略大小写关联
        batchProviderMeta.setIgnoreCaseToRef(true);
        //关联(数据库)表的主键的字段名，默认取id
        batchProviderMeta.setRelationTablePkField("id");
        //当未匹配到数据时，返回的值
        batchProviderMeta.setMismatchHandler(t -> "-");
        return batchProviderMeta;
    }

}
