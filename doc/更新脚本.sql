-- 2020.12.14
-- BPMC新增business_type
insert into `data_dictionary_value` (dd_code,firm_id,firm_code,order_number,name,code,state) values('log_business_type',1,'group',111,'流控中心','bpmc',1);

-- BPMC新增operationType
insert into `data_dictionary_value` (dd_code,firm_id,firm_code,order_number,name,code,state) values('operation_type',1,'group',112,'部署','deploy',1);
insert into `data_dictionary_value` (dd_code,firm_id,firm_code,order_number,name,code,state) values('operation_type',1,'group',113,'清空','clear',1);
