<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta charset="utf-8" />
    <title>${head_title}</title>
    <meta name="description" content="overview &amp; stats" />
    <link rel="shortcut icon" href="${contextPath}/favicon.ico" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
    <#css/>
    <#js/>
    <style>
        /** sweetAlert2 弹窗设置 */
        .swal2-container {
            z-index: 9999999
        }
        /** 我的任务 渐变色 */
         .gradient-title{
             /* Firefox 3.6+ */
             background: -moz-linear-gradient(top, #fff, #e1e1e1);
             /* Safari 4-5, Chrome 1-9 */
             /* -webkit-gradient(,  [, ]?,  [, ]? [, ]*) */
             background: -webkit-gradient(linear,top,from(#fff),to(#e1e1e1));
             /* Safari 5.1+, Chrome 10+ */
             background: -webkit-linear-gradient(top, #fff, #e1e1e1);
             /* Opera 11.10+ */
             background: -o-linear-gradient(top, #fff, #e1e1e1);
         }
    </style>
    <script type="text/javascript">
        var contextPath = '${contextPath}';
    </script>

</head>
<body>
<#loadingProgress/>
${tag.body}
</body>
</html>
