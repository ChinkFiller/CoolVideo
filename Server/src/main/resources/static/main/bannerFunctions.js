tableStyle=[[
    {field: 'bid', title: 'BID', width: "10%"},
    {field: 'type', title: '类型', width: "10%"},
    {field: 'url', title: '跳转网页', width: "15%"},
    {field: 'title', title: '标题', minWidth: "15%"},
    {field: 'img', title: '封面地址', minWidth: "30%"},
    {field: 'id', title: '视频id', minWidth: "10%"},
    {field: 'option', title: '操作', width: "10%",templet:
            '<div class="opbtn" >\n' +
            '        <i class="layui-icon layui-icon-edit" onclick="changeData(\'{{d.type}}\',\'{{d.url}}\',\'{{d.title}}\',\'{{d.img}}\',\'{{d.id}}\',\'{{d.bid}}\')"></i>\n' +
            '        <i class="layui-icon layui-icon-delete" onclick="removeData(apiAddrRemoveData,\'{{d.bid}}\')"></i>\n' +
            '</div>'
    }
]]

function changeData(type,url,title,img,id,bid){
    layui.layer.open({
        type: 1,
        area: ['420px','60%'], // 宽高
        content: '<form  class="layui-form" onsubmit="handelSubmit(event)" style="width: 90%; margin-left: 5%; margin-right: 5%;">\n' +
            '        <div style="display: flex;margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="BannerID" readonly name="bid" value='+`${bid}`+'></div>\n' +
            '        <select style="height: 35px;width: 100%;" name="type" lay-filter="all">\n'+
            '            <option value="video" '+`${type==="video"?'selected':''}`+'>video</option>\n' +
            '            <option value="web"'+`${type==="web"?'selected':''}`+'>web</option>\n' +
            '        </select>\n' +
            '<div id="other-function">'+
            '        <div style="display:'+`${type==="web"?'none':'flex'}`+';margin-top: 10px;margin-bottom: 10px"><input type="number" class="layui-input" placeholder="视频ID" name="id" value='+`${id}`+'></div>\n' +
            '        <div style="display:'+`${type==="video"?'none':'flex'}`+';margin-top: 10px;margin-bottom: 10px;"><input type="text" class="layui-input" placeholder="标题" name="title" value='+`${title}`+'></div>\n' +
            '        <div style="display:'+`${type==="video"?'none':'flex'}`+';margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="封面地址" name="img" value='+`${img}`+'></div>\n' +
            '        <div style="display:'+`${type==="video"?'none':'flex'}`+';margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="跳转地址" name="url" value='+`${url}`+'></div>\n' +
            '</div>'+
            '        <div style="margin-top: 35px">\n' +
            '            <button type="submit" class="layui-btn" style="margin-left: 25%;width: 20%;margin-right: 2.5%">确定</button>\n' +
            '            <button type="button" class="layui-btn" style="margin-right:25%;width:20%;margin-left: 2.5%" onclick="layer.closeAll(`page`)">取消</button>\n' +
            '        </div>\n' +
            '    </form>',
        success:()=>{
            layui.use('form', function(){
                var form = layui.form;
                form.render();
                form.on('select(all)',function(data){
                    if (data.value==="video"){
                        document.getElementById('other-function').innerHTML=
                            '        <div style="margin-top: 10px;margin-bottom: 10px"><input type="number" class="layui-input" placeholder="视频ID" name="id" value='+`${id}`+'></div>\n'
                    }else{
                        document.getElementById('other-function').innerHTML=
                            '        <div style="margin-top: 10px;margin-bottom: 10px;"><input type="text" class="layui-input" placeholder="标题" name="title" value='+`${title}`+'></div>\n' +
                            '        <div style="margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="封面地址" name="img" value='+`${img}`+'></div>\n' +
                            '        <div style="margin-top: 10px;margin-bottom: 10px"><input type="text" class="layui-input" placeholder="跳转地址" name="url" value='+`${url}`+'></div>\n'
                    }
                });
            });
        }
    })
}
function handelSubmit(event){
    event.preventDefault();
    const formData = new FormData(event.target);
    const jsonObject = {};
    formData.forEach((value, key) => {
        jsonObject[key] = value;
    });
    console.log(jsonObject)
    ajax(apiUploadData,"POST",jsonObject,{},(res)=>{
        if (!res.error){
            layui.layer.msg("数据修改成功")
            for (var i=0;i<alldata.length;i++){
                if (parseInt(alldata[i].bid)===parseInt(jsonObject['bid'])){
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

function handelSubmit(event){
    event.preventDefault();
    const formData = new FormData(event.target);
    const jsonObject = {};
    formData.forEach((value, key) => {
        jsonObject[key] = value;
    });
    console.log(jsonObject)
    ajax(apiUploadData,"POST",jsonObject,{},(res)=>{
        if (!res.error){
            layui.layer.msg("数据修改成功")
            for (var i=0;i<alldata.length;i++){
                if (parseInt(alldata[i].bid)===parseInt(jsonObject['bid'])){
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

function search(key){
    if (key===''){
        reloadTable(alldata)
        return;
    }
    if (isPureNumber(key)){
        for (var i=0;i<alldata.length;i++){
            if (parseInt(alldata[i].bid)===parseInt(key)){
                reloadTable([alldata[i]])
                return;
            }
        }
        reloadTable([])
        return;
    }
    var search_res=[]
    for (var i=0;i<alldata.length;i++){
        if (alldata[i].title!=null&&alldata[i].title.includes(key)){
            search_res.push(alldata[i])
        }
    }
    reloadTable(search_res)
}