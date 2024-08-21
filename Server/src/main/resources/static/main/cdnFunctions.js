var tableStyle=[[
    {field: 'id', title: 'ID', width: "10%"},
    {field: 'name', title: '名称', width: "40%"},
    {field: 'state', title: '状态', width: "15%"},
    {field: 'num', title: '已缓存的分集', minWidth: "15%"},
    {field: 'option', title: '操作', width: "20%",templet:
            '<div class="opbtn" >\n' +
            '        <i class="layui-icon layui-icon-search" onclick="lookVideo(\'{{d.id}}\',\'{{d.num}}\')"></i>\n' +
            '        <i class="layui-icon layui-icon-delete" onclick="removeData(apiAddrRemoveData,\'{{d.id}}\'+\'@\'+\'{{d.num}}\')"></i>\n' +
            '</div>'
    }
]]
function search(key){
    if (key===''){
        reloadTable(alldata)
        return;
    }
    if (isPureNumber(key)){
        var search_res=[]
        for (var i=0;i<alldata.length;i++){
            if (parseInt(alldata[i].id)===parseInt(key)){
                search_res.push(alldata[i])
            }
        }
        reloadTable(search_res)
        return;
    }
    var search_res=[]
    for (var i=0;i<alldata.length;i++){
        if (alldata[i].name!=null&&alldata[i].name.includes(key)){
            search_res.push(alldata[i])
        }
    }
    reloadTable(search_res)
}