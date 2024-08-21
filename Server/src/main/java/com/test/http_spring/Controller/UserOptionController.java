package com.test.http_spring.Controller;

import com.test.http_spring.pojo.film_data;
import com.test.http_spring.pojo.users;
import com.test.http_spring.service.FilmService;
import com.test.http_spring.service.MailService;
import com.test.http_spring.service.UserService;
import com.test.http_spring.utils.GlobalValue;
import com.test.http_spring.utils.ToolsFunction;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@RestController
public class UserOptionController {
    @Autowired
    UserService userService;
    @Autowired
    FilmService filmService;
    @Autowired
    MailService mailService;
    private ConcurrentHashMap<String,long[]> vcodes=new ConcurrentHashMap<>();

    //有bug，在输入不存在的id时
    @GetMapping("/agree")
    public java.util.Map agree(@RequestParam int mode, @RequestParam int id, @RequestHeader(value = "token") String token){
        users userdata=userService.getOneUserByToken(token);
        if (userdata==null){
            return ToolsFunction.backErrorMsg("Token is invalid");
        }
        List<String> agreedata=ToolsFunction.getJsonListData(userdata.getSetting(),"agree");
        //mode为1是点赞，mode为0是取消赞
        if (mode==1){
            //检查用户是否已经点赞
            if (agreedata.contains(String.valueOf(id))){
                return ToolsFunction.backErrorMsg("You already agree it");
            }
            //判断这个电影的id是否有效
            if (filmService.findFilmDataById(id)==null){
                return ToolsFunction.backErrorMsg("File id not Found");
            }
            filmService.addOneFilmAgree(id);
            agreedata.add(String.valueOf(id));

            userService.updateUserSetting(ToolsFunction.setJsonData(userdata.getSetting(),"agree",agreedata).toString(),token);
            return ToolsFunction.backNoneDataSuceessMsg();
        }
        if (mode==0){
            //检查用户是否已经点赞
            if (!agreedata.contains(String.valueOf(id))){
                return ToolsFunction.backErrorMsg("Error Option");
            }
            //判断这个电影的id是否有效
            if (filmService.findFilmDataById(id)==null){
                return ToolsFunction.backErrorMsg("File id not Found");
            }
            filmService.removeOneFilmAgree(id);
            agreedata.remove(String.valueOf(id));
            userService.updateUserSetting(ToolsFunction.setJsonData(userdata.getSetting(),"agree",agreedata).toString(),token);
            return ToolsFunction.backNoneDataSuceessMsg();
        }else {
            return ToolsFunction.backErrorMsg("Illegal parameter");
        }
    }

    @GetMapping ("/get_agree")
    public Map getAgree(@RequestParam("id") int id,@RequestHeader(value = "token") String token){
        HashMap back=new HashMap();
        film_data data=filmService.findFilmDataById(id);
        if (data==null){
            return ToolsFunction.backErrorMsg("Film not Found");
        }
        back.put("num",data.getAgree());
        users userdata=userService.getOneUserByToken(token);
        if (userdata==null){
            return ToolsFunction.backErrorMsg("Token is invalid");
        }
        List<String> agreedata=ToolsFunction.getJsonListData(userdata.getSetting(),"agree");
        if (agreedata.contains(String.valueOf(id))){
            back.put("is_agree",1);
        }else{
            back.put("is_agree",0);
        }
        return ToolsFunction.backSuccessDataMap(back);
    }

    //用户登录接口
    @PostMapping("/login")
    public java.util.Map login(@RequestBody String orbody){
        Map body=ToolsFunction.StringToMap(orbody);
        if (body==null){
            return ToolsFunction.backErrorMsg("Bad Request");
        }
        HashMap back=new HashMap();
        if (!body.containsKey("username") || !body.containsKey("password")){
            return ToolsFunction.backErrorMsg("Bad Request");
        }
        String username=body.get("username").toString();
        String password=body.get("password").toString();
        if (ToolsFunction.isSqlInjection(username)){
            return ToolsFunction.backErrorMsg("Bad Request");
        }
        users userdata=userService.getOneUserByName(username);
        if (userdata==null){
            back.put("login_state",0);
            back.put("msg","Username or Password is Incorrect");
            return ToolsFunction.backSuccessDataMap(back);
        }
        if (userdata.getPawd().equals(password)){
            String token= UUID.randomUUID().toString();
            userService.updateUserToken(token,body.get("username").toString());
            back.put("token",token);
            back.put("icon",userdata.getIcon());
            back.put("name",userdata.getName());
            back.put("login_state",1);
            back.put("msg","Login Success");
            return ToolsFunction.backSuccessDataMap(back);
        }else{
            back.put("login_state",0);
            back.put("msg","Username or Password is Incorrect");
            return ToolsFunction.backSuccessDataMap(back);
        }
    }


    //用户头像上传接口
    @PostMapping("/upload_icon")
    public java.util.Map pross_image(@RequestParam(value = "file",required = false) MultipartFile file, @RequestHeader(value = "token") String token){
        if (token==null || userService.getOneUserByToken(token)==null){
            return ToolsFunction.backErrorMsg("Token is invalid");
        }
        try {
            if (file==null||file.isEmpty() || !(file.getContentType().equals("image/jpeg")||file.getContentType().equals("image/png"))){
                return ToolsFunction.backErrorMsg("Bad Request");
            }
            String base64img=ToolsFunction.compressAndEncodeImage(file.getBytes(),100,100);
            userService.updateUserIcon("data:"+file.getContentType()+";base64,"+base64img,token);
            HashMap back=new HashMap();
            back.put("icon","data:"+file.getContentType()+";base64,"+base64img);
            return ToolsFunction.backSuccessDataMap(back);
        }catch (IOException e){
            return ToolsFunction.backErrorMsg("Can't write file!");
        }catch (MultipartException e2){
            return ToolsFunction.backErrorMsg("NO_FILE_UP_LOAD");
        }
    }

    @GetMapping("/rename")
    public Map rename(@RequestParam("name") String name,@RequestHeader(value = "token") String token){
        if (userService.getOneUserByToken(token)==null){
            return ToolsFunction.backErrorMsg("Token is invalid");
        }
        if (name.length()>9){
            return ToolsFunction.backErrorMsg("Name too long");
        }
        name= Encode.forHtmlContent(name);
        userService.updateUSerName(name,token);
        return ToolsFunction.backNoneDataSuceessMsg();
    }

    //验证码定期清理任务
    @Scheduled(fixedRate = 60000)
    public void clearDisableCode(){
        long nowTime=ToolsFunction.getTimeStamp();
        vcodes.entrySet().removeIf(entry -> nowTime - entry.getValue()[1] > 600);
    }

    @GetMapping("/get_vcode")//获取验证码的功能
    public Map get_vcode(@RequestParam("mailAddress") String mailAddress){
        if (userService.getOneUserByName(mailAddress)!=null){//判断邮箱是否已经存在
            return ToolsFunction.backErrorMsg("The account already exists");
        }
        Random rm=new Random();
        int vcode=rm.nextInt(899999)+100000;
        long nowTime=ToolsFunction.getTimeStamp();
        if (ToolsFunction.isEmail(mailAddress)){//判断是否为有效的邮箱地址
            if (vcodes.containsKey(mailAddress)){//判断是否已经发送过了验证码
                if (nowTime-vcodes.get(mailAddress)[1]>600){//判断是否超时
                    long[] data=new long[2];
                    data[0]=vcode;data[1]=nowTime;
                    vcodes.put(mailAddress,data);
                    SimpleMailMessage message=ToolsFunction.createNewMail(mailAddress, String.valueOf(vcode));
                    if (message==null){
                        return ToolsFunction.backErrorMsg("发送失败");
                    }
                    mailService.sendMail(message);
                    return ToolsFunction.backNoneDataSuceessMsg();
                }else{//未过期
                    return ToolsFunction.backNoneDataSuceessMsg();
                }
            }else {//新增一个邮箱地址
                if (vcodes.size()>400){
                    return ToolsFunction.backErrorMsg("vcode too many");
                }
                long[] data = new long[2];
                data[0] = vcode;
                data[1] = nowTime;
                vcodes.put(mailAddress, data);
                SimpleMailMessage message=ToolsFunction.createNewMail(mailAddress, String.valueOf(vcode));
                if (message==null){
                    return ToolsFunction.backErrorMsg("发送失败");
                }
                mailService.sendMail(message);
                return ToolsFunction.backNoneDataSuceessMsg();
            }
        }else {
            return ToolsFunction.backErrorMsg("EmailAddress is wrong");
        }
    }


    //用户检查token值是否有效的接口
    @GetMapping("/check_state")
    public HashMap check_token(@RequestHeader(value = "token") String token){
        if (userService.getOneUserByToken(token)==null){
            return ToolsFunction.backErrorMsg("login_disable");
        }else{
            return ToolsFunction.backNoneDataSuceessMsg();
        }
    }


    //用户注册接口
    @PostMapping("/register")
    public Map register(@RequestBody String orbody){
        Map body=ToolsFunction.StringToMap(orbody);
        if (body==null){
            return ToolsFunction.backErrorMsg("Bad Request");
        }
        if (!body.containsKey("username")||!body.containsKey("password")||!body.containsKey("vcode")){
            return ToolsFunction.backErrorMsg("Bad Request");
        }
        String username=body.get("username").toString();
        String password=body.get("password").toString();
        if (ToolsFunction.isSqlInjection(username)){
            return ToolsFunction.backErrorMsg("Bad Request");
        }
        if (!vcodes.containsKey(username)){//验证验证码是否存在
            return ToolsFunction.backErrorMsg("vcode error");
        }
        if (!body.get("vcode").toString().equals(String.valueOf(vcodes.get(username)[0]))){//验证验证码是否相同
            return ToolsFunction.backErrorMsg("vcode error");
        }
        if (ToolsFunction.getTimeStamp()-vcodes.get(username)[1]>600){//验证验证码是否过期
            System.out.println("删除了" + username + "的验证码信息");
            vcodes.remove(username);
            return ToolsFunction.backErrorMsg("vcode overdue");
        }
        vcodes.remove(username);//用完这个验证码，删除掉
        String tem_name=ToolsFunction.MD5(UUID.randomUUID().toString()).substring(0,5);
        users udata=new users();
        udata.setUser(username);
        udata.setPawd(password);
        udata.setName("用户-"+tem_name);
        udata.setVip(0);
        udata.setIcon(GlobalValue.icon);
        userService.createUser(udata);
        HashMap back=new HashMap();
        String token=UUID.randomUUID().toString();
        userService.updateUserToken(token,body.get("username").toString());
        back.put("token",token);
        back.put("icon", GlobalValue.icon);
        back.put("name","用户-"+tem_name);
        back.put("login_state",1);
        back.put("msg","Login Success");
        return ToolsFunction.backSuccessDataMap(back);
    }

    //上传账号追番
    @PostMapping("/upload_allow")
    public Map uploadAllow(@RequestBody String orbody,@RequestHeader String token){
        List allowData=ToolsFunction.getJsonListData(orbody,"data");
        if (allowData==null){
            return ToolsFunction.backError(400);
        }
        users userdata=userService.getOneUserByToken(token);
        if (userdata==null){
            return ToolsFunction.backError(403);
        }
        List<String> datas=new ArrayList<>();
        for (int i=0;i<allowData.size();i++){
            if (allowData.get(i) instanceof Map<?,?>){
                datas.add(((Map<?, ?>) allowData.get(i)).get("id").toString());
                continue;
            }
            if (!(allowData.get(i) instanceof String||allowData.get(i) instanceof Integer)) {
                return ToolsFunction.backError(400);
            }
            datas.add(allowData.get(i).toString());
        }
        datas.add(0,String.valueOf(ToolsFunction.getTimeStamp()));
        userService.updateUserSetting(ToolsFunction.setJsonData(userdata.getSetting(),"allow",datas).toString(),token);
        return ToolsFunction.backNoneDataSuceessMsg();
    }

    //上传账号历史记录
    @PostMapping("/upload_history")
    public Map uploadHistory(@RequestBody String orbody,@RequestHeader String token){
        List historyData=ToolsFunction.getJsonListData(orbody,"data");
        if (historyData==null){
            return ToolsFunction.backError(400);
        }
        users userdata=userService.getOneUserByToken(token);
        if (userdata==null){
            return ToolsFunction.backError(403);
        }
        List datas=new ArrayList();
        for (int i=0;i<historyData.size();i++){
            if (!(historyData.get(i) instanceof Map)){
                return ToolsFunction.backError(400);
            }
            if (!(((Map<?, ?>) historyData.get(i)).containsKey("id")&&((Map<?, ?>) historyData.get(i)).containsKey("state"))){
                return ToolsFunction.backError(400);
            }
            HashMap oneData=new HashMap();
            oneData.put("id",((Map<?, ?>) historyData.get(i)).get("id"));
            oneData.put("state",((Map<?, ?>) historyData.get(i)).get("state"));
            datas.add(oneData);
        }
        datas.add(0,ToolsFunction.getTimeStamp());
        userService.updateUserSetting(ToolsFunction.setJsonData(userdata.getSetting(),"history",datas).toString(),token);
        return ToolsFunction.backNoneDataSuceessMsg();
    }


    //获取追账号追番
    @GetMapping("/get_allow")
    public Map getAllow(@RequestHeader String token){
        users userdata=userService.getOneUserByToken(token);
        if (userdata==null){
            return ToolsFunction.backError(403);
        }
        Boolean hasChange=false;
        List<String> allowData=ToolsFunction.getJsonListData(userdata.getSetting(),"allow");
        Iterator<String> iterator = allowData.iterator();
        //默认跳过时间记录
        iterator.next();
        List datas=new ArrayList<>();
        while (iterator.hasNext()) {
            String item = iterator.next();
            film_data filmData=filmService.findFilmDataById(Integer.parseInt(item));
            if (filmData==null){
                hasChange=true;
                iterator.remove();
            }else{
                HashMap oneData=new HashMap();
                oneData.put("img_url",filmData.getImg_url());
                oneData.put("title",filmData.getName());
                oneData.put("state",filmData.getState());
                oneData.put("id",Integer.valueOf(item));
                datas.add(oneData);
            }
        }
        datas.add(0,allowData.get(0));
        if (hasChange){
            userService.updateUserSetting(ToolsFunction.setJsonData(userdata.getSetting(),"allow",allowData).toString(),token);
        }
        return ToolsFunction.backSuccessDataList(datas);
    }

    //获取账号历史记录
    @GetMapping("/get_history")
    public Map getHistory(@RequestHeader String token){
        users userdata=userService.getOneUserByToken(token);
        if (userdata==null){
            return ToolsFunction.backError(403);
        }
        Boolean hasChange=false;
        List<Map> allowData=ToolsFunction.getJsonListData(userdata.getSetting(),"history");
        List<Map> orData=ToolsFunction.getJsonListData(userdata.getSetting(),"history");
        Iterator<Map> iterator = allowData.iterator();
        //默认跳过时间记录
        iterator.next();
        while (iterator.hasNext()) {
            Map item = iterator.next();
            film_data filmData=filmService.findFilmDataById(Integer.parseInt(item.get("id").toString()));
            if (filmData==null){
                hasChange=true;
                iterator.remove();
                orData.remove(item);
            }else{
                item.put("img_url",filmData.getImg_url());
                item.put("title",filmData.getName());
                item.put("full",filmData.getState());
            }
        }
        if (hasChange){
            userService.updateUserSetting(ToolsFunction.setJsonData(userdata.getSetting(),"history",orData).toString(),token);
        }
        return ToolsFunction.backSuccessDataList(allowData);
    }

}
