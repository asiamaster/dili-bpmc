package com.dili.bpmd.provider;

import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.BatchProviderMeta;
import com.dili.ss.metadata.provider.BatchDisplayTextProviderSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 任务办理人提供者
 * @Author: WangMi
 * @Date: 2019/12/17
 * @Description:
 */
@Component
public class TaskAssigneeProvider extends BatchDisplayTextProviderSupport {

    @Autowired
    private TaskRpc taskRpc;

    @Override
    protected BatchProviderMeta getBatchProviderMeta(Map metaMap) {
        BatchProviderMeta batchProviderMeta = DTOUtils.newInstance(BatchProviderMeta.class);
        batchProviderMeta.setEscapeFiled("assigneeName");
        batchProviderMeta.setFkField("processInstanceId");
        //忽略大小写关联
        batchProviderMeta.setIgnoreCaseToRef(true);
        //关联(数据库)表的主键的字段名，默认取id
        batchProviderMeta.setRelationTablePkField("processInstanceId");
        batchProviderMeta.setMismatchHandler((t) -> {
            return null;
        });
        return batchProviderMeta;
    }

    @Override
    protected List getFkList(List<String> relationIds, Map metaMap) {
        //根据业务号查询任务
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceIds(relationIds);
        BaseOutput<List<TaskMapping>> output = taskRpc.listTaskMapping(taskDto);
        if(!output.isSuccess()){
            return new ArrayList(0);
        }
        return output.getData();
    }
}
