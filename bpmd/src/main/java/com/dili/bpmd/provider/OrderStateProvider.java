package com.dili.bpmd.provider;

import com.dili.bpmd.consts.OrderState;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValueProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 订单状态提供者
 * @Author: WangMi
 * @Date: 2019/12/4 10:12
 * @Description:
 */
@Component
public class OrderStateProvider implements ValueProvider {
    @Override
    public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
        return null;
    }

    @Override
    public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
        return val == null ? null : OrderState.getDesc((Integer)val);
    }
}
