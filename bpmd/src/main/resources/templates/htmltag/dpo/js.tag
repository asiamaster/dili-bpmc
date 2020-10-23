<script>
    /**
     *
     * @Date 2020-10-21
     * @author asiamaster
     *
     ***/

    /*********************变量定义区 begin*************/
    //行索引计数器
    //如 let itemIndex = 0;
    let _grid = $('#grid');
    let _form = $('#_form');
    let _modal = $('#_modal');

    /*********************变量定义区 end***************/


    /******************************驱动执行区 begin***************************/
    $(function () {
        $(window).resize(function () {
            _grid.bootstrapTable('resetView')
        });
        queryDataHandler();
    });
    /******************************驱动执行区 end****************************/

    /*****************************************函数区 begin************************************/
    /**
     * 打开新增窗口
     */
    function openInsertHandler() {
        _form[0].reset();
        $("#_id").val("");
        $('#_type').attr('disabled', false);
        $('#_dateFormat').attr('disabled', false);
        $('#_length').attr('disabled', false);
        _modal.modal('show');
        _modal.find('.modal-title').text('编号规则新增');
    }

    /**
     * 打开修改窗口
     */
    function openUpdateHandler(row) {
        if(!row){
            //获取选中行的数据
            let rows = _grid.bootstrapTable('getSelections');
            if (null == rows || rows.length == 0) {
                bs4pop.alert('请选中一条数据');
                return;
            }
            row = rows[0];
        }
        _modal.modal('show');
        _modal.find('.modal-title').text('修改订单');
        let formData = $.extend({}, row);
        formData = bui.util.addKeyStartWith(bui.util.getOriginalData(formData), "_");
        bui.util.loadFormData(formData);
    }

    /**
     *  保存及更新表单数据
     */
    function saveOrUpdateHandler() {
        if (_form.validate().form() != true) {
            return;
        }
        bui.loading.show('努力提交中，请稍候。。。');
        let _formData = bui.util.removeKeyStartWith(_form.serializeObject(true), "_");
        let _url = null;
        //没有id就新增
        if (_formData.id == null || _formData.id == "") {
            _url = "${contextPath}/dynamicProcessOrders/insert.action";
        } else {//有id就修改
            _url = "${contextPath}/dynamicProcessOrders/update.action";
        }
        $.ajax({
            type: "POST",
            url: _url,
            data: _formData,
            processData: true,
            dataType: "json",
            async: true,
            success: function (data) {
                bui.loading.hide();
                if (data.success) {
                    _grid.bootstrapTable('refresh');
                    _modal.modal('hide');
                } else {
                    bs4pop.alert(data.result, {type: 'error'});
                }
            },
            error: function (a, b, c) {
                bui.loading.hide();
                bs4pop.alert('远程访问失败', {type: 'error'});
            }
        });
    }

    /**
     * 查询处理
     */
    function queryDataHandler() {
        _grid.bootstrapTable('refreshOptions', {url: '/dynamicProcessOrders/listPage.action'});
    }

    /**
     * 取消
     */
    function doCancelHandler() {
        doAjax("确定要取消吗？", "${contextPath}/dynamicProcessOrders/cancel.action");
    }

    /**
     * 提交付款
     */
    function doSubmitHandler() {
        doAjax("确定要提交付款吗？", "${contextPath}/dynamicProcessOrders/submit.action");
    }

    /**
     * 提交审批
     */
    function doSubmitApprovalHandler() {
        doAjax("确定要提交审批吗？", "${contextPath}/dynamicProcessOrders/submitApproval.action");
    }

    /**
     * 确认付款
     */
    function doPaidHandler() {
        doAjax("确定要付款吗？", "${contextPath}/dynamicProcessOrders/paid.action");
    }

    /**
     * 补录
     */
    function doSupplementHandler() {
        doAjax("确定要补录吗？", "${contextPath}/dynamicProcessOrders/supplement.action");
    }

    /**
     * 撤回
     */
    function doWithdrawHandler() {
        doAjax("确定要撤回吗？", "${contextPath}/dynamicProcessOrders/withdraw.action");
    }

    /**
     * 到期
     */
    function doExpiredHandler() {
        doAjax("确定要手动设置订单到期吗？", "${contextPath}/dynamicProcessOrders/expired.action");
    }


    /**
     * ajax提交操作
     */
    function doAjax(confirmMsg, url, data) {
        //获取选中行的数据
        let rows = _grid.bootstrapTable('getSelections');
        if (null == rows || rows.length == 0) {
            bs4pop.alert('请选中一条数据');
            return;
        }
        let selectedRow = rows[0];
        bs4pop.confirm(confirmMsg, undefined, function (sure) {
            if(sure){
                bui.loading.show('努力提交中，请稍候。。。');
                $.ajax({
                    type: "POST",
                    url: url,
                    data:{id: selectedRow.id, code: selectedRow.code, processInstanceId: selectedRow.processInstanceId},
                    processData:true,
                    dataType: "json",
                    async : true,
                    success : function(data) {
                        bui.loading.hide();
                        if(data.success){
                            _grid.bootstrapTable('refresh');
                            _modal.modal('hide');
                        }else{
                            bs4pop.alert(data.result, {type: 'error'});
                        }
                    },
                    error : function() {
                        bui.loading.hide();
                        bs4pop.alert('远程访问失败', {type: 'error'});
                    }
                });
            }
        })
    }

    /**
     *  显示流程图
     */
    function showProgress() {
        //获取选中行的数据
        let rows = _grid.bootstrapTable('getSelections');
        if (null == rows || rows.length == 0) {
            bs4pop.alert('请选中一条数据');
            return;
        }
        let selected = rows[0];
        //var href = '<#config name="bpmc.server.address"/>/api/runtime/progress?processInstanceId='+selected.processInstanceId+'&processDefinitionId='+selected.processDefinitionId+"&"+Math.random();
        //$("#processInstanceImg").attr("src", href);
        let url = '<#config name="bpmc.server.address"/>/api/runtime/progress?processInstanceId='+selected.processInstanceId+'&processDefinitionId='+selected.processDefinitionId+"&"+Math.random();
        dia = bs4pop.dialog({
            title: '流程图',
            content: url,
            isIframe : true,
            closeBtn: true,
            backdrop : 'static',
            width: '95%',
            height : '95%',
            btns: []
        });
    }

    /**
     * table参数组装
     * 可修改queryParams向服务器发送其余的参数
     * @param params
     */
    function queryParams(params) {
        let temp = {
            rows: params.limit,   //页面大小
            page: ((params.offset / params.limit) + 1) || 1, //页码
            sort: params.sort,
            order: params.order
        }
        return $.extend(temp, bui.util.bindGridMeta2Form('grid', 'queryForm'));
    }

    /*****************************************函数区 end**************************************/

    /*****************************************自定义事件区 begin************************************/
    //表单弹框关闭事件
    _modal.on('hidden.bs.modal', function () {
        _form[0].reset();
        //重置表单验证到初始状态
        $(this).find('input,select,textarea').removeClass('is-invalid is-valid');
        $(this).find('input,select,textarea').removeAttr('disabled readonly');
        $(this).find('.invalid-feedback').css('display','none');
    });

    _grid.on('dbl-click-cell.bs.table', function ($element, field, value, row) {
        openUpdateHandler(row);
    });

    //选中行事件
    _grid.on('check.bs.table', function (e, row, $element) {
        //先禁用所有按键
        $('#toolbar button').attr('disabled', true);
        //允许新增按钮
        $('#btn_add').attr('disabled', false);
        //有流程实例id就可以查看流程图
        if(row.processInstanceId) {
            $("#btn_progress").attr('disabled', false);
        }
        var url = "${contextPath}/dynamicProcessOrders/listEventName.action";
        $.ajax({
            type: "POST",
            url: url,
            data: {processInstanceId: row.processInstanceId, state: row.$_state},
            processData: true,
            dataType: "json",
            async: true,
            success: function (output) {
                bui.loading.hide();
                if (output.success) {
                    for(var i in output.data){
                        $("."+output.data[i]).attr('disabled', false);
                    }
                    _grid.bootstrapTable('refresh');
                    _modal.modal('hide');
                } else {
                    bs4pop.alert(output.result, {type: 'error'});
                }
            },
            error: function (a, b, c) {
                bui.loading.hide();
                bs4pop.alert('远程访问失败', {type: 'error'});
            }
        });
    });
    /*****************************************自定义事件区 end**************************************/
</script>