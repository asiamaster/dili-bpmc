package com.dili.bpmc.servicetask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.dili.http.okhttp.OkHttpUtils;
import com.dili.ss.domain.BaseOutput;
import okhttp3.Response;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 远程服务任务
 * @Author: WangMi
 * @Date: 2019/12/3 15:36
 * @Description:
 */
@Service
public class RestfulService implements JavaDelegate {

    public final static Logger log = LoggerFactory.getLogger(RestfulService.class);
    //restful URL
    private Expression url;
    //重试次数
    private Expression retry;
    //重试间隔(秒)
    private Expression interval;
    @Override
    public void execute(DelegateExecution execution) {
        Response resp = null;
        Object urlObj = url.getValue(execution);
        if(urlObj == null){
            return;
        }
        //默认重试三次
        int retryInt = 3;
        //默认三秒重试
        long intervalLong = 3000;
        //重试次数
        try {
            if(retry != null) {
                retryInt = Integer.parseInt(retry.getValue(execution).toString().trim());
            }
            if(interval != null) {
                intervalLong = Long.parseLong(interval.getValue(execution).toString().trim() + "000");
            }
        }catch (Exception e){
            //数字转换异常时，使用默认参数
        }
        while(retryInt > 0) {
            try {
                resp = OkHttpUtils.post().url(urlObj.toString()).params((Map) execution.getVariables()).build()
                        //30秒过期
                        .connTimeOut(1000L * 30L).readTimeOut(1000L * 30L).writeTimeOut(1000L * 30L).execute();
                if (resp.isSuccessful()) {
                    String result = resp.body().string();
                    BaseOutput<Map<String, Object>> baseOutput = JSON.parseObject(result, BaseOutput.class, Feature.IgnoreNotMatch);
                    if (baseOutput.isSuccess()) {
                        execution.setVariables(baseOutput.getData());
                        break;
                    } else {
                        retryInt--;
                        log.error(String.format("远程调用[" + urlObj.toString() + "]内部异常,message:[%s]", baseOutput.getMessage()));
                        Thread.sleep(intervalLong);
                    }
                } else {
                    retryInt--;
                    log.error(String.format("远程调用[" + urlObj.toString() + "]发生失败,%s秒后重试,code:[%s], message:[%s]", intervalLong/1000L, resp.code(), resp.message()));
                    Thread.sleep(intervalLong);
                }
            } catch (IOException | InterruptedException e) {
                log.error(String.format("远程调用[" + url + "]发生异常,message:[%s],%s秒后重试", e.getMessage(), intervalLong/1000L));
                retryInt--;
                try {
                    Thread.sleep(intervalLong);
                } catch (InterruptedException ex) {
                    //dont care
                }
            }
        }
//        System.out.println(new StringBuilder().append("实例id[").append(execution.getProcessInstanceId()).append("],活动id[").append(execution.getCurrentActivityId()).append("]，订单[").append(execution.getVariables().get("orderCode")).append("]已生效").toString());
    }

}
