# 1. 启动流程 
```
//注入运行时服务
@Autowired
private RuntimeService runtimeService;
//获取流程实例对象
ProcessInstance pi = runtimeService.startProcessInstanceByKey(ProcessConstants.CUSTOMER_ORDER_PROCESS_KEY, customerOrderCode);
```

# 2. 按照流程实例id申领任务并流转到下一节点
//注入服务
```
@Autowired
private CustomerOrderProcessService customerOrderProcessService;
//调用流转方法
processInstanceId:流程实例id
variables:流程变量
customerOrderProcessService.clainAndCompleteTaskByPi(processInstanceId, variables);
```

//流程变量参数variables的取值
```
运营商确认指派订单给承运商，否则取消客户订单
allocate=true
//承运商确认接单，否则退回运营商
accept=true
//承运单确认指派，否则打回客户单重新指派
confirmAssign=true
```

# 3. (司机)发送异常消息
```
messageName:transportExceptionReport(消息名称，写死)
processInstanceId:流程实例id
variables:流程变量，这里为null
CustomerOrderProcessService.messageEventReceived(String messageName, String processInstanceId, Map variables)
```

# 4. 状态流转提示:
- 创建货单时，客户订单状态需要判断所有商品和件数都已处理，才修改状态为已处理
- 发货登记时，客户订单状态需要判断所有商品都已处理，才修改为已处理
- 到货确认时，客户订单状态需要判断所有商品为已发车，才修改为已发车
- 客户签收时，客户订单状态需要判断所有商品都已到货，才修改状态为已到货
- 流程结束时，客户订单状态需要判断所有商品都已签收，才修改状态为已签收

# 5. 流程图跟踪
访问URL:
```
processInstanceId:流程实例id(必填)
processDefinitionId: 流程定义id(选填)
http://oms.tms.nong12.com:8082/pi/progress.action?processInstanceId='+processInstanceId+'&processDefinitionId='+processDefinitionId
```