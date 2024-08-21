
function getNowFormatDate() {
  let date = new Date(),
    year = date.getFullYear(),
    month = date.getMonth() + 1,
    strDate = date.getDate()
  if (month < 10) month = `0${month}`
  if (strDate < 10) strDate = `0${strDate}`

  return `${year}-${month}-${strDate}`
}
function encode_data(data){
    var key=getNowFormatDate();
    var length=key.length-1
    var data_length=data.length
    var count=0
    var last=''
    for (var i=0;i<data_length;i++){
        var b = data.charCodeAt(i)
        if (count==length){count=0}
        b=b-(key.charCodeAt(count)%10)+3
        last=last+String.fromCharCode(b)
        count++
    }
    return last
}
function decode_data(data){
    var key=getNowFormatDate();
    var length=key.length-1
    var data_length=data.length
    var count=0
    var last=''
    for (var i=0;i<data_length;i++){
        var b = data.charCodeAt(i)
        if (count==length){count=0}
        b=b-3+(key.charCodeAt(count)%10)
        last=last+String.fromCharCode(b)
        count++
    }
    return last
}

function getLoginAddress(){
    var code=window.location.pathname.split("/").at(-1);
    return window.location.protocol+"//"+window.location.host+"/CoolVideoAdmin/login/"+code+"@login"
}
function passEnterKey(event) {
        event = event || window.event;
        if (event.keyCode == 13) {
            document.getElementById("loginBtn").click();
        }
    }

function userEnterKey(event) {
    event = event || window.event;
    if (event.keyCode == 13) {
        document.getElementById("passwd").focus()
    }
}

function setCookie(cname,cvalue,exdays) {
    var d = new Date();
    d.setTime(d.getTime()+(exdays*24*60*60*1000));
    var expires = "expires="+d.toGMTString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

window.onload=function() {
    var username_under = document.getElementById('name');
    var passsword_under = document.getElementById('passwd');
    var user_underline = document.getElementById('userline');
    var pswd_underline = document.getElementById('passline');
    var sign=false;
    username_under.onfocus = function () {
            username_under.style.borderColor = 'silver';
            passsword_under.style.borderColor = 'silver';
            user_underline.style.background = '#4158D0';
            pswd_underline.style.background = '#4158D0';
            username_under.onchange=function (){
                if (sign){
                    document.getElementById("passwd").value="";
                    sign=false;
                }
            }
    }
    username_under.blur=function(){
        user_underline.style.background = 'solid silver';
        pswd_underline.style.background = 'solid silver';
    }
    document.getElementById('loginBtn').onclick = function () {
        const username = document.getElementById('name').value;
        const password = document.getElementById('passwd').value;
        if (username === '' || password === '') {
            username_under.style.borderColor = 'red';
            passsword_under.style.borderColor = 'red';
            user_underline.style.background = 'red';
            pswd_underline.style.background = 'red';
            return;
        }
        fetch(getLoginAddress(),{
            method:"POST",
            headers:{
                "Content-type":"application/json"
            },
            body:JSON.stringify({"username":encode_data(username),"password":encode_data(password)})
        }).then(respose=>{
            if (!respose.ok){
                username_under.style.borderColor = 'red';
                passsword_under.style.borderColor = 'red';
                user_underline.style.background = 'red';
                pswd_underline.style.background = 'red';
                showAlert("未知错误")
                throw new Error("Request Fail")
            }else{
                return respose.json()
            }
        }).then(data=>{
            if (data.error===0){
                setCookie('token',decode_data(data.data.token),1);
                localStorage.setItem("userdata",JSON.stringify({"icon":data.data.icon,"name":data.data.name}))
                console.log(data.data.icon)
                console.log(data.data.name)
                console.log(JSON.parse(localStorage.getItem("userdata")))
                window.location.reload();
            }else{
                username_under.style.borderColor = 'red';
                passsword_under.style.borderColor = 'red';
                user_underline.style.background = 'red';
                pswd_underline.style.background = 'red';
                sign=true;
                showAlert(data.msg)
            }
        }).catch(err=>{
            username_under.style.borderColor = 'red';
            passsword_under.style.borderColor = 'red';
            user_underline.style.background = 'red';
            pswd_underline.style.background = 'red';
            showAlert(err)
            console.error(err)
        })
    //     var httpRequest = new XMLHttpRequest();
    //     httpRequest.open('post', getLoginAddress(), true);
    //     let data = {'username': encode_data(username), 'password': encode_data(password)}
    //     httpRequest.setRequestHeader("Content-type", "application/json");
    //     httpRequest.send(JSON.stringify(data));
    //     httpRequest.onerror = function (e) {
    //         alert('网络错误！')
    //     };
    //     httpRequest.onreadystatechange = function () {
    //         if (httpRequest.readyState === 4 && httpRequest.status === 200) {
    //             var backdata = httpRequest.responseText
    //             var obj = JSON.parse(backdata);
    //             if (obj['status'] == 'success') {
    //                 setCookie('token',decode_data(obj['key']),1);
    //                 location.reload();
    //             }
    //             else{
    //
    //                 alert('未知错误');
    //             }
    //         }
    //         if (httpRequest.readyState === 4 && httpRequest.status === 401){
    //                 username_under.style.borderColor = 'red';
    //                 passsword_under.style.borderColor = 'red';
    //                 user_underline.style.background = 'red';
    //                 pswd_underline.style.background = 'red';
    //                 sign=true;
    //         }
    //         if (httpRequest.readyState === 4 && httpRequest.status === 404) {
    //             alert('网络错误！')
    //         }
    //     }
    // }
    }
}