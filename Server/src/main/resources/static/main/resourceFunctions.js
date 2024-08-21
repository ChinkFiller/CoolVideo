var tableStyle=[[
    {field: 'id', title: 'id', width: "10%"},
    {field: 'name', title: '影片名称', width: "40%"},
    {field: 'img_url', title: '封面地址', minWidth: "20%"},
    {field: 'state', title: '更新状态', minWidth: "10%"},
    {field: 'agree', title: '点赞数量', width: "10%",sort: true},
    {field: 'option', title: '操作', width: "10%",templet:
            '<div class="opbtn" >\n' +
            '        <i class="layui-icon layui-icon-edit" onclick="changeData(\'{{d.id}}\',\'{{d.name}}\',\'{{d.state}}\',\'{{d.leader}}\',\'{{d.actor}}\',\'{{d.img_url}}\',\'{{d.agree}}\')"></i>\n' +
            '        <i class="layui-icon layui-icon-delete" onclick="removeData(apiAddrRemoveData,\'{{d.id}}\')"></i>\n' +
            '</div>'
    }
]]



function search(key){
    if (isPureNumber(key)){
        for (var i=0;i<alldata.length;i++){
            if (alldata[i].id===parseInt(key)){
                reloadTable([alldata[i]])
                return;
            }
        }
        reloadTable([])
        return;
    }
    var search_res=[]
    for (var i=0;i<alldata.length;i++){
        if (alldata[i].name.includes(key)){
            search_res.push(alldata[i])
        }
    }
    reloadTable(search_res)
}

function handelSubmit(event){
    event.preventDefault();
    const formData = new FormData(event.target);
    const jsonObject = {};
    formData.forEach((value, key) => {
        jsonObject[key] = value;
    });
    ajax(apiUploadData,"POST",jsonObject,{},(res)=>{
        if (!res.error){
            layui.layer.msg("数据修改成功")
            for (var i=0;i<alldata.length;i++){
                if (parseInt(alldata[i].id)===parseInt(jsonObject['id'])){
                    alldata[i]=jsonObject
                    reloadTable(alldata)
                    break;
                }
            }
            layui.layer.closeAll('page')
        }else{
            layui.layui.msg(res.msg)
            layui.layer.closeAll('page')
        }
    })
}

function changeData(id,name,state,leader,actor,img_url,agree){
    layui.layer.open({
        type: 1,
        area: ['420px', '420px'], // 宽高
        content: '<form class="data-change" action="/CoolVideoAdmin/api/[[${code}]]/updateData" method="post" onsubmit="handelSubmit(event)">\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="名称" name="name" value='+`${name}`+'></div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="更新状态" name="state" value='+`${state}`+'></div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="导演" name="leader" value='+`${leader}`+'></div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="演员名称" name="actor" value='+`${actor}`+'></div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="封面地址" name="img_url" value='+`${img_url}`+'></div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px;visibility: hidden;height: 5px;position: relative"><input type="text" class="layui-input" placeholder="ID" name="id" hidden="hidden" value='+`${id}`+'></div>\n' +
            '        <div>\n' +
            '            <button type="submit" class="layui-btn" style="margin-left: 25%;width: 20%;margin-right: 2.5%">确定</button>\n' +
            '            <button type="button" class="layui-btn" style="margin-right:25%;width:20%;margin-left: 2.5%" onclick="layer.closeAll(`page`)">取消</button>\n' +
            '        </div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px;visibility: hidden;height: 5px;position: relative"><input type="text" class="layui-input" placeholder="" name="agree" value='+`${agree}`+'></div>\n' +
            '    </form>'
    })
}