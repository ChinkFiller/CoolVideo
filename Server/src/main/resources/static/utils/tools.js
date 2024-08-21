function getCookie(name) {
    const nameEQ = name + "=";
    const cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i].trim();
        if (cookie.indexOf(nameEQ) == 0) {
            return cookie.substring(nameEQ.length, cookie.length);
        }
    }
    return null;
}

function ajax(url,method="GET",body={},header={},success){
    var data={}
    header["token"]=getCookie("token")
    if (method==="GET"){
        data={
            method:method,
            headers:header
        }
        let getData="?"
        for (var key in body){
            getData+=`${key}=${body[key]}&`
        }
        url+=getData
    }else{
        data={
            method:method,
            headers:header,
            body:JSON.stringify(body)
        }
    }
    fetch(url,data).then(response=>{
        if (!response.ok){
            success({error:true})
            throw new Error("Request Failed")
        }
        return response.json()
    }).then(data=>{
        if (data.error===1){
            success({error:true,msg:data.msg})
        }else{
            success({error:false,data:data.data})
        }
    }).catch(err=>{
        throw new Error(err)
    })
}

function uploadFile(file,url,data,success,progress) {
    const xhr = new XMLHttpRequest();

    // 监听上传进度
    xhr.upload.addEventListener('progress', function(e) {
        if (e.lengthComputable) {
            const percentComplete = (e.loaded / e.total) * 100;
            progress(percentComplete)
        }
    });

    // 监听请求完成事件
    xhr.addEventListener('load', function() {
        if (xhr.status >= 200 && xhr.status < 300) {
            success({error:false})
        } else {
            success({error:true})
        }
    });

    // 监听请求错误事件
    xhr.addEventListener('error', function() {
        success({error:true})

    });

    // 监听请求取消事件
    xhr.addEventListener('abort', function() {
        success({error:true})
    });

    xhr.open('POST', url, true);
    // xhr.setRequestHeader('Content-Type', 'multipart/form-data;boundary=----WebKitFormBoundaryk4ZvuPo6pkphe7Pl');
    xhr.setRequestHeader("token",getCookie('token'))

    // 创建 FormData 对象
    const formData = new FormData();
    formData.append('file', file);
    for (var i in data){
        formData.append(i,data[i])
    }
    xhr.send(formData);
}
var alldata=[]
function loadData(url){
    var lodding=layui.layer.load()
    ajax(url,"GET",{},{},(res)=>{
        layui.layer.close(lodding)
        if (!res.error){
            alldata=res.data
            reloadTable(alldata)
        }else{
            layui.layer.msg(res.msg)
        }
    })
}

function removeData(url,msg){
    layui.layer.confirm("确定删除这条数据吗？将无法恢复",{btn:["删除","取消"],title: "删除信息"},()=>{
        ajax(url,"GET",{id:msg},{},(res)=>{
            if (!res.error){
                loadData(apiAddrGetData)
                layui.layer.msg("删除成功")
            }else{
                layui.layer.msg(res.msg)
            }
        })
    })
}
function finishUpload(state){
    if (state){
        layui.layer.msg("数据提交成功")
    }else{
        layui.layer.msg("数据提交失败")
    }
    loadData(apiAddrGetData)
    layui.layer.closeAll('iframe')
}

function lookVideo(id,num){
    ajax("/get_video_url","GET",{id:id,num:num,cdn:1},{},(res)=>{
        if (!res.error){
            window.open(res.data.url)
        }
    })
}
function handelKeypass(e){
    if (e.key=="Enter"){
        search(document.getElementById("search_bar").value)
    }
}
function addOneData(url,title){
    layui.layer.open({
        type: 2,
        title: title,
        shadeClose: true,
        shade: 0.8,
        area: ['420px', '60%'],
        content:url
    });
}

function isPureNumber(str) {
    return !isNaN(str) && str.trim() !== '' && Number(str) == str;
}
function reloadTable(data){
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#resource-msg',
            cols: tableStyle,
            data:data,
            page: true
        });
    });
}

layui.form.on('submit(demo1)', function(data){
    var lodding=layui.layer.load()
    var test=data.field
    test["id"]=555
    ajax(apiUpload, "POST", test, {}, (res) => {
        layui.layer.close(lodding)
        if (!res.error) {
            layui.layer.msg("数据提交成功")
            window.parent.finishUpload(true);
        } else {
            layui.layer.msg("数据提交失败")
            window.parent.finishUpload(false);
        }
    })
    return false;
});