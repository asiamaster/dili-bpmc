<style>
    .mask {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        opacity: 0.3;
        filter: alpha(opacity=30);
        display: none;
        background: #ccc;
    }
    .mask-msg {
        position: absolute;
        top: 50%;
        margin-top: -20px;
        padding: 10px 5px 10px 30px;
        height: 36px;
        border-width: 2px;
        border-style: solid;
        display: none;
        border-color: #D4D4D4;
        background: #ffffff 5px center;
    }
</style>
<script type="text/javascript">
    <%if(cover){%>
    $(function () {
        //弹出加载层
        $("<div class=\"mask\"></div>").css({
            display: "block",
            position: "absolute",
            left: "0%",
            top: 0,
            width: $(".class-allNow-content-right").width(),
            // height: $(".class-allNow-content-right").height()
            height:"100%"
        }).appendTo("#${cid}");
        $("<div class=\"mask-msg\"></div>").html("请先签收任务...").css({
            position: "fixed",
            left: "50%",
            top: ($(window).height() / 2 + 20),
            "line-height": "16px",
            "-moz-opacity": "0.65",
            "opacity": ".65",
            "filter": "alpha(opacity=65)",
            width: "155px"
        }).appendTo("#${cid}").css({display: "block"});
    })
    <%}%>
</script>
