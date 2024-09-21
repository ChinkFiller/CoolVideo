var tableStyle=[[
    {field: 'user', title: '用户名', width: "10%"},
    {field: 'pawd', title: '密码', minWidth: "15%"},
    {field: 'name', title: '昵称', minWidth: "10%"},
    {field: 'vip', title: '权限等级', width: "10%",sort: true},
    {field: 'speedtimes', title: '加速次数', width: "10%"},
    {field: 'viptime', title: '会员到期时间', width: "20%"},
    {field: 'option', title: '操作', width: "15%",templet:
        '<div class="opbtn" >\n' +
        '     <i class="layui-icon layui-icon-edit" onclick="changeData(\'{{d.user}}\',\'{{d.pawd}}\',\'{{d.name}}\',\'{{d.vip}}\',\'{{d.viptime}}\',\'{{d.speedtimes}}\')"></i>\n' +
        '     <i class="layui-icon layui-icon-delete" onclick="removeData(apiAddrRemoveData,\'{{d.user}}\')"></i>\n' +
        '</div>'
    }
]]


function search(key){
    var search_res=[]
    for (var i=0;i<alldata.length;i++){
        if (alldata[i].name.includes(key) ||alldata[i].user.includes(key)){
            search_res.push(alldata[i])
        }
    }
    reloadTable(search_res)
}

function changeData(user,pawd,name,vip,date,speedtimes){
    layui.layer.open({
        type: 1,
        area: ['420px', '370px'], // 宽高
        content: '<form class="layui-form" onsubmit="handelSubmit(event)" style="width: 90%; margin-left: 5%; margin-right: 5%;">'+
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" readonly placeholder="用户名" name="user" value='+`${user}`+'></div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="密码" name="pawd" value='+`${pawd}`+'></div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="昵称" name="name" value='+`${name}`+'></div>\n' +
            '        <select style="height: 35px;width: 100%;" name="vip">\n' +
            '            <option value="0" '+`${parseInt(vip)===0?'selected':''}`+'>0</option>\n' +
            '            <option value="1"'+`${parseInt(vip)===1?'selected':''}`+'>1</option>\n' +
            '            <option value="2"'+`${parseInt(vip)===2?'selected':''}`+'>2</option>\n' +
            '        </select>\n' +
            '        <input name="speedtimes" value="'+speedtimes+'" style="display: none">'+
            '        <input style="margin-top: 10px;margin-bottom: 10px" type="text" class="layui-input" id="ID-laydate-demo" placeholder="yyyy-MM-dd" name="viptime">\n' +
            '        <div style="margin-top: 35px">\n' +
            '            <button type="submit" class="layui-btn" style="margin-left: 25%;width: 20%;margin-right: 2.5%">确定</button>\n' +
            '            <button type="button" class="layui-btn" style="margin-right:25%;width:20%;margin-left: 2.5%" onclick="layer.closeAll(`page`)">取消</button>\n' +
            '        </div>\n'+
            '    </form>',
        success:()=>{
            layui.use('form', function(){
                var form = layui.form;
                form.render();
            });
            layui.laydate.render({
                elem: '#ID-laydate-demo',
                value:date
            });
        }
    })
}

function handelSubmit(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const jsonObject = {};
    formData.forEach((value, key) => {
        jsonObject[key] = value;
    });
    ajax(apiUploadData,"POST",jsonObject, {}, (res) => {
        if (!res.error) {
            for (var i = 0; i < alldata.length; i++) {
                if (alldata[i].user === jsonObject['user']) {
                    alldata[i] = jsonObject
                    reloadTable(alldata)
                    break;
                }
            }
            layui.layer.msg("数据修改成功")
            layui.layer.closeAll('page')
        } else {
            layui.layer.msg(res.msg)
            layui.layer.closeAll('page')
        }
    })
}


