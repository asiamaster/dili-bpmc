package com.dili.bpmc.servicetask;

import com.dili.ss.domain.BaseOutput;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Ribbon远程服务任务
 * @Author: WangMi
 * @Date: 2020/3/24 10:53
 * @Description:
 */
@Service
@ConditionalOnBean(RestTemplate.class)
public class RibbonService implements JavaDelegate {

    public final static Logger log = LoggerFactory.getLogger(RibbonService.class);
    //ribbon URL
    private Expression url;
    //重试次数
    private Expression retry;
    //重试间隔(秒)
    private Expression interval;
    @Autowired
    RestTemplate restTemplate;
    @Override
    public void execute(DelegateExecution execution) {
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
                ResponseEntity<BaseOutput> responseEntity = restTemplate.getForEntity(urlObj.toString(), BaseOutput.class, execution.getVariables());
                if(HttpStatus.OK.equals(responseEntity.getStatusCode())){
                    BaseOutput<Map<String, Object>> baseOutput = responseEntity.getBody();
                    if (baseOutput.isSuccess()) {
                        execution.setVariables(baseOutput.getData());
                        break;
                    } else {
                        retryInt--;
                        log.error(String.format("ribbon调用[" + urlObj.toString() + "]内部异常, code:[%s], message:[%s]", baseOutput.getCode(), baseOutput.getMessage()));
                        Thread.sleep(intervalLong);
                    }
                }else{
                    retryInt--;
                    log.error(String.format("ribbon调用[" + urlObj.toString() + "]发生失败,%s秒后重试,StatusCodeValue:[%s]", intervalLong/1000L, responseEntity.getStatusCodeValue()));
                    Thread.sleep(intervalLong);
                }
            } catch (InterruptedException e ) {
                log.error(String.format("远程调用[" + url + "]发生异常,message:[%s],%s秒后重试", e.getMessage(), intervalLong/1000L));
                retryInt--;
                try {
                    Thread.sleep(intervalLong);
                } catch (InterruptedException ex) {
                    //dont care
                }
            }
        }
    }

}
