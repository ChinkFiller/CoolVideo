var tableStyle=[[
    {field: 'id', title: 'ID', width: "10%"},
    {field: 'user', title: '举报人', width: "20%",templet: `<div style="overflow: hidden;text-overflow: ellipsis">{{d.user}}</div>`},
    {field: 'content', title: '内容', width: "30%"},
    {field: 'banuser', title: '发布用户', width: "15%"},
    {field: 'time', title: '最后举报时间', minWidth: "15%"},
    {field: 'option', title: '是否放行', width: "10%",templet:
            '<div class="opbtn" >\n' +
            '        <i class="layui-icon layui-icon-ok" onclick="passOneDanmu(\'NO@{{d.id}}\')"></i>\n' +
            '        <i class="layui-icon layui-icon-close" onclick="deleteOneDanmu(\'OK@{{d.id}}\')"></i>\n' +
            '</div>'
    }
]]

function passOneDanmu(id){
    layui.layer.confirm("确定放行这个弹幕吗?",{btn:["确定","取消"],title: "审核信息"},()=>{
        ajax(apiAddrRemoveData,"GET",{id:id},{},(res)=>{
            if (!res.error){
                loadData(apiAddrGetData)
                layui.layer.msg("操作成功")
            }else{
                layui.layer.msg(res.msg)
            }
        })
    })
}
function deleteOneDanmu(id){
    layui.layer.confirm("确定清除这个弹幕吗?",{btn:["确定","取消"],title: "审核信息"},()=>{
        ajax(apiAddrRemoveData,"GET",{id:id},{},(res)=>{
            if (!res.error){
                loadData(apiAddrGetData)
                layui.layer.msg("操作成功")
            }else{
                layui.layer.msg(res.msg)
            }
        })
    })
}