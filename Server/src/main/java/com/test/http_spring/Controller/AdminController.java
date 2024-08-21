package com.test.http_spring.Controller;


import com.test.http_spring.pojo.Banner;
import com.test.http_spring.pojo.FastFunction;
import com.test.http_spring.pojo.film_data;
import com.test.http_spring.pojo.users;
import com.test.http_spring.service.AdService;
import com.test.http_spring.service.BadMsgService;
import com.test.http_spring.service.FilmService;
import com.test.http_spring.service.UserService;
import com.test.http_spring.utils.GlobalValue;
import com.test.http_spring.utils.ToolsFunction;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;


//管理员的界面
@Controller
@RequestMapping("/CoolVideoAdmin")
public class AdminController {
    @Autowired
    UserService userService;
    @Autowired
    FilmService filmService;
    @Autowired
    AdService adService;
    @Autowired
    BadMsgService badMsgService;

    public AdminController() {
        ToolsFunction.warningLog("管理后台地址已设置为/CoolVideoAdmin/index/" + GlobalValue.randomAdminPath);
    }

    //主页判断
    @GetMapping("/index/{path}")
    public String mainindex(@PathVariable String path, @CookieValue(value = "token", defaultValue = "null") String token, Model model) {
        if (path.equals(GlobalValue.randomAdminPath)) {
            users data = userService.getOneUserByToken(token);
            if (data != null && data.getVip() == 2) {
                model.addAttribute("code", GlobalValue.randomAdminPath);
                return "index";
            } else {
                return "login";
            }
        }
        ToolsFunction.backError(404);
        //其他访问返回403
        return "403";
    }


    //子界面管理界面路由
    @GetMapping("/index/{path}/{pagename}")
    public String hello(@PathVariable String path,
                        @PathVariable String pagename,
                        @CookieValue(value = "token", defaultValue = "null") String token,
                        Model model) {
        if (!path.equals(GlobalValue.randomAdminPath)) {
            //其他访问返回404,这个目的是触发一个404错误
            ToolsFunction.backError(404);
        }
        //权限检查，访问其他子路径时检查其权限
        if (token.equals("null") || userService.getOneUserByToken(token) == null || userService.getOneUserByToken(token).getVip() != 2) {
            return "403";
        }
        //判断路径是否注册，不存在返回404
        if (!GlobalValue.childPageNameList.containsKey(pagename)) {
            //权限认证通过了，这里返回正常的404界面
            return "404";
        }
        //用于把信息返回前端
        model.addAttribute("code", GlobalValue.randomAdminPath);
        model.addAttribute("speedLimit", GlobalValue.speedLimite);
        model.addAttribute("port", GlobalValue.BootSetting.get("port")==null ?8080:GlobalValue.BootSetting.get("port"));
        model.addAttribute("databasetype", GlobalValue.BootSetting.get("database-type"));
        model.addAttribute("databaseaddr", GlobalValue.BootSetting.get("database-position"));
        model.addAttribute("emailaddr", GlobalValue.BootSetting.get("mail-username"));
        model.addAttribute("version", GlobalValue.version);
        model.addAttribute("dataScript","/static/main/"+GlobalValue.childPageNameList.get(pagename).get(0));
        model.addAttribute("getApiName",GlobalValue.childPageNameList.get(pagename).get(1));
        model.addAttribute("removeApiName",GlobalValue.childPageNameList.get(pagename).get(2));
        model.addAttribute("addApiName",GlobalValue.childPageNameList.get(pagename).get(3));
        model.addAttribute("updateApiName",GlobalValue.childPageNameList.get(pagename).get(4));
        model.addAttribute("addTitle",GlobalValue.childPageNameList.get(pagename).get(5));
        model.addAttribute("title",GlobalValue.childPageNameList.get(pagename).get(6));
        model.addAttribute("sysType",ToolsFunction.getSysDetailType());
        if (pagename.equals("welcome")){
            return "childPage/welcome";
        }
        if (pagename.contains("add")){
            return "childPage/"+pagename;
        }
        return  "childPage/control-model";
    }


    //登录函数
    @PostMapping("/login/{path}")
    @ResponseBody
    public Map adminLogin(@RequestBody String orbody, @PathVariable String path) {
        if (path.equals(GlobalValue.randomAdminPath + "@login")) {
            Map body = ToolsFunction.StringToMap(orbody);
            if (!ToolsFunction.judgeDataLegal(body,new String[]{"username","password"})) {
                return ToolsFunction.backErrorMsg("Bad Request");
            }
            String username = ToolsFunction.decodeData(body.get("username").toString());
            String password = ToolsFunction.decodeData(body.get("password").toString());
            if (ToolsFunction.isSqlInjection(username)){
                return ToolsFunction.backErrorMsg("Bad Request");
            }
            users userdata = userService.getOneUserByName(username);
            if (userdata == null) {
                return ToolsFunction.backErrorMsg("Password or Username Error");
            }
            if (userdata.getPawd().equals(password)) {
                if (userdata.getVip() == 2) {
                    HashMap data = new HashMap();
                    String token = UUID.randomUUID().toString();
                    userService.updateUserToken(token, username);
                    data.put("token", ToolsFunction.encodeData(token));
                    data.put("icon", userdata.getIcon());
                    data.put("name", userdata.getName());
                    return ToolsFunction.backSuccessDataMap(data);
                } else {
                    return ToolsFunction.backErrorMsg("Permission Refuse");
                }
            } else {
                return ToolsFunction.backErrorMsg("Password or Username Error");
            }
        } else {
            return ToolsFunction.backErrorMsg("Not Found");
        }
    }



    //GET方法的总api
    @GetMapping("/api/{path}/{function}")
    @ResponseBody
    public Map getSysState(@PathVariable String path,
                           @PathVariable String function,
                           @RequestHeader(value = "token") String token,
                           @RequestParam(value = "id", required = false, defaultValue = "null") String id) {
        if (!path.equals(GlobalValue.randomAdminPath)) {
            ToolsFunction.backError(404);
        }
        if (token.equals("null") || userService.getOneUserByToken(token) == null || userService.getOneUserByToken(token).getVip() != 2) {
            ToolsFunction.backError(403);
        }
        if (function.equals("sysState")) {
            HashMap data = new HashMap();
            data.put("cpu", "%.1f".formatted(ToolsFunction.getCpuUsage()));
            data.put("memory", "%.1f".formatted(ToolsFunction.getPhysicalMemoryUsage()));
            data.put("storage", "%.1f".formatted(ToolsFunction.getStorageUsage()));
            data.put("autoGetter",GlobalValue.autoGetterState);
            data.put("getterLogs",GlobalValue.getterLogs);
            return ToolsFunction.backSuccessDataMap(data);
        }
        if (function.equals("getAllVideo")) {
            return ToolsFunction.backSuccessDataList(filmService.findAllFilm());
        }
        if (function.equals("getAllUser")) {
            return ToolsFunction.backSuccessDataList(userService.findAllUser());
        }
        if (function.equals("getAllBanner")) {
            return ToolsFunction.backSuccessDataList(adService.getAllBannerData());
        }
        if (function.equals("getAllFastFunctions")) {
            return ToolsFunction.backSuccessDataList(adService.getAllFastFunctionSetting());
        }if (function.equals("getAllBadMsg")) {
            return ToolsFunction.backSuccessDataList(badMsgService.findAllBadMsg());
        }
        if (function.equals("getter")) {
            if (id.equals("null")|| id.isEmpty()) {
                ToolsFunction.backError(400);
            }
            if (id.equals("boot")){
                filmService.bootAutoGetter();
                try {
                    Thread.sleep(900);
                    if (GlobalValue.Getter==null){
                        return ToolsFunction.backError(500);
                    }else{
                        return ToolsFunction.backNoneDataSuceessMsg();
                    }
                }catch (Exception e){
                    return ToolsFunction.backError(500);
                }
            }
            if (id.equals("stop")){
                if (!(GlobalValue.autoGetterState.equals("running")||GlobalValue.autoGetterState.equals("retrying"))){
                    return ToolsFunction.backNoneDataSuceessMsg();
                }else{
                    ToolsFunction.terminateGetter();
                    return ToolsFunction.backNoneDataSuceessMsg();
                }
            }
            return ToolsFunction.backError(400);
        }
        if (function.equals("removeData")) {
            try {
                if (id.equals("null")|| id.isEmpty()) {
                    ToolsFunction.backError(400);
                }
                filmService.removeOneFilm(Integer.parseInt(id));
                return ToolsFunction.backNoneDataSuceessMsg();
            } catch (Exception e) {
                ToolsFunction.backError(500);
            }
        }
        if (function.equals("removeUser")) {
            try {
                if (id.equals("null")|| id.isEmpty()) {
                    ToolsFunction.backError(400);
                }
                userService.removeOneUser(id);
                return ToolsFunction.backNoneDataSuceessMsg();
            } catch (Exception e) {
                ToolsFunction.backError(500);
            }
        }
        if (function.equals("removeBanner")) {
            try {
                if (id.equals("null")|| id.isEmpty()) {
                    ToolsFunction.backError(400);
                }
                adService.removeBanner(Integer.parseInt(id));
                return ToolsFunction.backNoneDataSuceessMsg();
            } catch (Exception e) {
                ToolsFunction.backError(500);
            }
        }
        if (function.equals("removeFastFunction")) {
            try {
                if (id.equals("null")|| id.isEmpty()) {
                    ToolsFunction.backError(400);
                }
                adService.removeFastFunction(Integer.parseInt(id));
                return ToolsFunction.backNoneDataSuceessMsg();
            } catch (Exception e) {
                ToolsFunction.backError(500);
            }
        }
        //获取本地缓存的所有信息
        if (function.equals("getCdnData")){
            if (!GlobalValue.canUserCdn){
                return ToolsFunction.backErrorMsg("Local CDN is not available");
            }

            ArrayList<Map<String,String>> datas=new ArrayList();
            List<Map<String,String>> fileData=ToolsFunction.getLocalCdnData();
            try {
                if (fileData==null||fileData.isEmpty()){
                    return ToolsFunction.backSuccessDataList(new ArrayList<>());
                }
                for (Map<String,String> oneData:fileData){
                    film_data oneFileData=filmService.findFilmDataById(Integer.parseInt(oneData.get("id")));
                    oneData.put("name",oneFileData.getName());
                    oneData.put("state",oneFileData.getState());
                    datas.add(oneData);
                }
                return ToolsFunction.backSuccessDataList(datas);
            }catch (Exception e){
                System.out.println(e);
                return ToolsFunction.backErrorMsg("Server CDN System Error");
            }
        }


        if (function.equals("removeCdnData")){
            if (!GlobalValue.canUserCdn){
                return ToolsFunction.backErrorMsg("Local CDN is not available");
            }
            try {
                if (id.equals("null")|| id.isEmpty()) {
                    ToolsFunction.backError(400);
                }
                String realId=id.split("@")[0];
                String num=id.split("@")[1];
                File data=new File("video/"+realId+"/"+num+".mp4");
                if (data.delete()){
                    File dir=new File("video/"+realId);
                    if (dir.list()!=null&&dir.list().length==0){
                        dir.delete();
                    }
                    return ToolsFunction.backNoneDataSuceessMsg();
                }else{
                    return ToolsFunction.backErrorMsg("Delete Failed");
                }
            }catch (Exception e){
                return ToolsFunction.backErrorMsg("Bad Request");
            }
        }


        if (function.equals("removeBadMsg")){
            if (id.equals("null")|| id.isEmpty()||id.split("@").length!=2) {
                ToolsFunction.backError(400);
            }
            String State=id.split("@")[0];
            String realid=id.split("@")[1];
            try {
                badMsgService.deleteOneDanmu(Integer.parseInt(realid),State.equals("OK"));
                return ToolsFunction.backNoneDataSuceessMsg();
            }catch (Exception e){
                return ToolsFunction.backError(500);
            }
        }

        if (function.equals("getAllBadWords")){
            return ToolsFunction.backSuccessDataList(GlobalValue.badWords);
        }
        return ToolsFunction.backError(404);
    }




    //api中post用的所有方法
    @PostMapping("/api/{path}/{function}")
    @ResponseBody
    public Map updateData(@PathVariable String path,
                          @PathVariable String function,
                          @RequestBody String ordata,
                          @RequestHeader(value = "token") String token){
        //检查随机路径匹配
        if (!path.equals(GlobalValue.randomAdminPath)) {
            ToolsFunction.backError(404);
        }

        //权限验证
        if (token.equals("null") || userService.getOneUserByToken(token) == null || userService.getOneUserByToken(token).getVip() != 2) {
            ToolsFunction.backError(403);
        }
        Map data = ToolsFunction.StringToMap(ordata);



        //添加和修改视频资源
        if (function.equals("addData")||function.equals("updateData")){
            if (!ToolsFunction.judgeDataLegal(data, new String[]{"name", "state", "leader", "actor", "img_url","id"})) {
                ToolsFunction.backError(400);
            }
            try {
                film_data f_data = ToolsFunction.writedata(data);
                if (function.equals("addData")){
                    filmService.addOneFilm(f_data);
                }else{
                    filmService.updateData(f_data);
                }
                return ToolsFunction.backNoneDataSuceessMsg();
            } catch (Exception e) {
                ToolsFunction.backError(500);
            }
        }


        //添加或者更新用户数据
        if (function.equals("updateUser")||function.equals("addUser")){
            if (!ToolsFunction.judgeDataLegal(data, new String[]{"user", "name", "pawd", "vip"})) {
                ToolsFunction.backError(400);
            }
            users uData=new users();
            if (function.equals("updateUser")){
                uData = userService.getOneUserByName(data.get("user").toString());
                if (uData==null){
                    ToolsFunction.backError(400);
                }
            }
            uData.setUser(data.get("user").toString());
            uData.setName(data.get("name").toString());
            uData.setVip(Integer.parseInt(data.get("vip").toString()));
            uData.setPawd(data.get("pawd").toString());
            if (function.equals("addUser")){
                uData.setIcon(GlobalValue.icon);
            }
            try {
                if (function.equals("addUser")){
                    userService.createUser(uData);
                }else{
                    userService.updataOneUser(uData);
                }
                return ToolsFunction.backNoneDataSuceessMsg();
            } catch (Exception e) {
                ToolsFunction.backError(500);
            }
        }


        //Banner的添加与修改
        if (function.equals("addBanner")||function.equals("updateBanner")){
            if (!ToolsFunction.judgeDataLegal(data, new String[]{"type"})) {
                ToolsFunction.backError(400);
            }
            if (data.get("type").equals("video")){
                if (!ToolsFunction.judgeDataLegal(data, new String[]{"id"})) {
                    ToolsFunction.backError(400);
                }
                Banner banner=new Banner();
                banner.setType("video");
                banner.setId(Integer.parseInt(data.get("id").toString()));
                try {
                    if (function.equals("addBanner")){
                        adService.addBanner(banner);
                    }else{
                        banner.setBid(Integer.parseInt(data.get("bid").toString()));
                        adService.updateBanner(banner);
                    }
                    return ToolsFunction.backNoneDataSuceessMsg();
                }catch (Exception e){
                    ToolsFunction.backError(500);
                }
            }
            if (data.get("type").equals("web")){
                if (!ToolsFunction.judgeDataLegal(data, new String[]{"title","img","url"})) {
                    ToolsFunction.backError(400);
                }
                Banner banner=new Banner();
                banner.setType("web");
                banner.setUrl(data.get("url").toString());
                banner.setImg(data.get("img").toString());
                banner.setTitle(data.get("title").toString());
                try {
                    if (function.equals("addBanner")){
                        adService.addBanner(banner);
                    }else{
                        banner.setBid(Integer.parseInt(data.get("bid").toString()));
                        adService.updateBanner(banner);
                    }
                    return ToolsFunction.backNoneDataSuceessMsg();
                }catch (Exception e){
                    ToolsFunction.backError(500);
                }
            }else{
                ToolsFunction.backError(400);
            }
        }
        if (function.equals("addFastFunction")||function.equals("updateFastFunction")){
            if (!ToolsFunction.judgeDataLegal(data, new String[]{"title", "img", "href"})) {
                ToolsFunction.backError(400);
            }
            try {
                FastFunction f_data = new FastFunction();
                if (function.equals("updateFastFunction")&&!ToolsFunction.judgeDataLegal(data, new String[]{"fid"})){
                    ToolsFunction.backError(400);
                    return ToolsFunction.backError(400);
                }
                if (function.equals("updateFastFunction")){
                    f_data.setFid(Integer.parseInt(data.get("fid").toString()));
                }
                f_data.setTitle(data.get("title").toString());
                f_data.setImg(data.get("img").toString());
                f_data.setHref(data.get("href").toString());
                if (function.equals("addFastFunction")){
                    adService.addFastFunction(f_data);
                }else{
                    adService.updateFastFunction(f_data);
                }
                return ToolsFunction.backNoneDataSuceessMsg();
            } catch (Exception e) {
                ToolsFunction.backError(500);
            }
        }

        if (function.equals("addBadWord")){
            if (!ToolsFunction.judgeDataLegal(data,new String[]{"badWords"})){
                return ToolsFunction.backError(400);
            }
            if (ToolsFunction.changeBadWords(data.get("badWords").toString())){
                return ToolsFunction.backNoneDataSuceessMsg();
            }else{
                return ToolsFunction.backError(500);
            }
        }
        return ToolsFunction.backError(404);
    }

    //文件上传接口
    @PostMapping("/file/{path}/{pagename}")
    @ResponseBody
    public Map fileHandle(@RequestParam(value = "file",required = false) MultipartFile file,
                          @RequestParam(value = "id") int id,
                          @RequestParam(value = "part")int part,
                          @PathVariable String path,
                          @PathVariable String pagename,
                          @RequestHeader(value = "token") String token, HttpServletRequest request){
        //检查随机路径匹配
        if (!path.equals(GlobalValue.randomAdminPath)) {
            ToolsFunction.backError(404);
        }

        //权限验证
        if (token.equals("null") || userService.getOneUserByToken(token) == null || userService.getOneUserByToken(token).getVip() != 2) {
            ToolsFunction.backError(403);
        }
        if (pagename.equals("addCdnFile")){
            if (file==null||file.isEmpty()){
                return ToolsFunction.backError(400);
            }
            if (!(file.getContentType()!=null&&file.getContentType().equals("video/mp4"))){
                return ToolsFunction.backError(400);
            }
            if (filmService.findFilmDataById(id)==null||part<=0){
                return ToolsFunction.backError(400);
            }
            try {
                File dataDir=new File("video/"+id);
                if (!dataDir.exists()){
                    if (!dataDir.mkdir()){
                        return ToolsFunction.backErrorMsg("Server IO Error");
                    }
                }
                File data=new File("video/%s/%s.mp4".formatted(id,part));
                if (!data.exists()){
                    if (!data.createNewFile()){
                        return ToolsFunction.backErrorMsg("Server IO Error");
                    }
                }
                FileOutputStream fw=new FileOutputStream(data);
                fw.write(file.getBytes(),0,file.getBytes().length);
                fw.flush();
                fw.close();
                return ToolsFunction.backNoneDataSuceessMsg();
            }catch (Exception e){
                System.out.println(e);
                return ToolsFunction.backErrorMsg("Server Error");
            }
        }
        return ToolsFunction.backError(404);
    }
}
