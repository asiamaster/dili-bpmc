 //如果父页面是任务中心
 if(parent.location.href='<#config name="bpmc.server.address"/>/task/taskCenter.html') {
     //向任务中心发送消息，参数为要跳转的地址
     window.parent.postMessage('<#config name="bpmc.server.address"/>/task/taskCenter.html', '<#config name="bpmc.server.address"/>');
 }else{
     location.href = "${href}";
 }