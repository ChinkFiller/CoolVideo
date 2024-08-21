tableStyle=[[
    {field: 'fid', title: 'FID', width: "10%"},
    {field: 'title', title: '标题', minWidth: "20%"},
    {field: 'href', title: '跳转链接', minWidth: "30%"},
    {field: 'img', title: '图标', width: "25%"},
    {field: 'option', title: '操作', width: "15%",templet:
            '<div class="opbtn" >\n' +
            '        <i class="layui-icon layui-icon-edit" onclick="changeData(\'{{d.fid}}\',\'{{d.title}}\',\'{{d.href}}\',\'{{d.img}}\')"></i>\n' +
            '        <i class="layui-icon layui-icon-delete" onclick="removeData(apiAddrRemoveData,\'{{d.fid}}\')"></i>\n' +
            '</div>'
    }
]]
function changeData(fid,title,href,img){
    layui.layer.open({
        type: 1,
        area: ['420px', '370px'], // 宽高
        content: '<form class="layui-form" onsubmit="handelSubmit(event)" style="width: 90%; margin-left: 5%; margin-right: 5%;">'+
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" readonly placeholder="FID" name="fid" value='+`${fid}`+'></div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="标题" name="title" value='+`${title}`+'></div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="跳转链接" name="href" value='+`${href}`+'></div>\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="图标地址" name="img" value='+`${img}`+'></div>\n' +
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
            loadData();
            layui.layer.msg("数据修改成功")
            layui.layer.closeAll('page')
        } else {
            layui.layer.msg(res.msg)
            layui.layer.closeAll('page')
        }
    })
}

function search(key) {
    if (key === '') {
        reloadTable(alldata)
        return;
    }
    if (isPureNumber(key)) {
        for (var i = 0; i < alldata.length; i++) {
            if (alldata[i].fid === parseInt(key)) {
                reloadTable([alldata[i]])
                return;
            }
        }
        reloadTable([])
        return;
    }
    var search_res = []
    for (var i = 0; i < alldata.length; i++) {
        if (alldata[i].title != null && alldata[i].title.includes(key)) {
            search_res.push(alldata[i])
        }
    }
    reloadTable(search_res)
}