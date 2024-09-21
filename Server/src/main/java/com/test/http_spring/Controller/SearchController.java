package com.test.http_spring.Controller;

import com.test.http_spring.pojo.BadMsg;
import com.test.http_spring.pojo.film_data;
import com.test.http_spring.pojo.users;
import com.test.http_spring.service.BadMsgService;
import com.test.http_spring.service.FilmService;
import com.test.http_spring.service.UserService;
import com.test.http_spring.utils.GlobalValue;
import com.test.http_spring.utils.ToolsFunction;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SearchController {

    //使用film服务
    @Autowired
    FilmService filmService;
    @Autowired
    UserService userService;
    @Autowired
    BadMsgService badMsgService;

    private ConcurrentHashMap<String,String> urlDatas=new ConcurrentHashMap<>();
    //模糊搜索某个信息
    @GetMapping("/search_data")
    public java.util.Map get_data(@RequestParam("key") String key){
        try {
            if (key.isEmpty()||key.equals(" ")){
                return ToolsFunction.backSuccessDataList(new ArrayList());
            }else{
                return ToolsFunction.backSuccessDataList(filmService.findFilmLikeByName(key));
            }
        }catch (RuntimeException e){
           return ToolsFunction.backErrorMsg("");
        }
    }

    //通过id查询某一条数据
    @GetMapping("/search_data_id")
    public java.util.Map get_data_id(@RequestParam("id") int id) {
        try {
            film_data data=filmService.findFilmDataById(id);
            if (data==null){
                return ToolsFunction.backErrorMsg("Film Not Found");
            }
            return ToolsFunction.backSuccessDataFilmData(data);
        }catch (RuntimeException e){
            return ToolsFunction.backErrorMsg("");
        }
    }

    //获取推荐的视频列表
    @GetMapping("/get_command_video")
    public Map get_command_video() {
        try {
            ArrayList<film_data> datas=new ArrayList<>();
            Random rm=new Random();
            List<film_data> all_films=filmService.findAllFilm();
            if (all_films.isEmpty()){
                   for (int i=0;i<10;i++){
                       datas.add(null);
                   }
            }else{
                for (int i=0;i<10;i++){
                    film_data data=all_films.get(rm.nextInt(all_films.size()-1));
                    while (data==null){
                        data=all_films.get(rm.nextInt(all_films.size()-1));
                    }
                    datas.add(data);
                }
            }
            return ToolsFunction.backSuccessDataList(datas);
        }catch (RuntimeException e){
            return ToolsFunction.backErrorMsg("");
        }
    }
    @Scheduled(fixedRate = 60000)
    public void deleteDisableUrl(){
        urlDatas.entrySet().removeIf(entry -> ToolsFunction.checkUrlState(entry.toString()));
    }


    //获取视频链接，并缓存的函数
    private HashMap getVideoUrl(String id,String num){
        HashMap back=new HashMap();
        film_data data=filmService.findFilmDataById(Integer.parseInt(id));
        if (data==null){
            return ToolsFunction.backErrorMsg("Data not Found");
        }
        String code=id+":"+num;
        if (urlDatas.containsKey(code)&&ToolsFunction.checkUrlState(urlDatas.get(code))){
            back.put("url",urlDatas.get(code));
            back.put("isOther",1);
            back.put("cdn",0);
            return back;
        }
        String url= ToolsFunction.get_video_url(id,num,data.getName());
        if (url.equals("error")){
            return ToolsFunction.backErrorMsg("Data not Found");
        }
        urlDatas.put(code,url);
        back.put("url",url);
        back.put("cdn",0);
        back.put("isOther",1);
        return back;
    }


    //获取视频链接的api
    @GetMapping("/get_video_url")
    public Map get_url(@RequestParam(value = "id") String id,@RequestParam("num") String num,@RequestParam(value = "cdn",required = false,defaultValue = "null") String cdn,@RequestHeader(value = "token",required = false,defaultValue = "null") String token){
        HashMap back=new HashMap<>();
        int line=cdn.equals("1")?1:0;
        if (token.equals("null")){
            return ToolsFunction.backSuccessDataMap(getVideoUrl(id,num));
        }else{
            users userdata=userService.getOneUserByToken(token);
            if (userdata==null){
                return ToolsFunction.backSuccessDataMap(getVideoUrl(id,num));
            }
            if (cdn.equals("null")&&userdata.getVip()>=1){line=1;}
            if (line==1){
                File file=new File("video/"+id+"/"+num+".mp4");
                if (file.exists()){//解析判断是否本地拥有资源，有的话就走本地，没有的话就走外链解析或者CDN
                    back.put("url","/videos/get_film?id="+id+"&num="+num+"&sign="+ ToolsFunction.MD5("AADM@conyafertools"+id+"@"+num+(System.currentTimeMillis()/1000)/18000));
                    back.put("isOther",0);
                    back.put("cdn",1);
                    return ToolsFunction.backSuccessDataMap(back);
                }else{//本地缓存不存在，自动更换外链解析或者CDN存储
                    back=getVideoUrl(id,num);
                    if (userdata.getSpeedtimes()>=GlobalValue.maxSpeedTimes&&userdata.getVip()==0){
                        return ToolsFunction.backSuccessDataMap(getVideoUrl(id,num));
                    }
                    if (!(userdata.getVip() == 2 || userdata.getVip() == 1)) {
                        userService.setUserTimes(token);
                    }
                    back.put("cdn",1);
                    back.put("url","https://proxy.conyafertools.work/get_video.mp4?url="+ToolsFunction.encode(back.get("url").toString())+"&token="+ToolsFunction.MD5(back.get("url").toString()+"AADM@Conyafer"));
                    return ToolsFunction.backSuccessDataMap(back);
                }
            }else{
                return ToolsFunction.backSuccessDataMap(getVideoUrl(id,num));
            }
        }
    }

    //获取弹幕信息
    @GetMapping("/getdanmu")
    public Map getDanmu(@RequestParam(value = "id") int id,@RequestParam("part") int part){
        JSONArray datas=filmService.getDanmu(id,String.valueOf(part));
        if (datas==null){
            return ToolsFunction.backErrorMsg("ID Not Found");
        }else{
            return ToolsFunction.backSuccessDataList(ToolsFunction.convertJsonArrayToList(datas));
        }
    }


    //发送弹幕信息
    @PostMapping("/setdanmu")
    public Map setDanmu(@RequestBody String orbody,@RequestHeader("token") String token){
        users userData=userService.getOneUserByToken(token);
        if (userData==null){
            return ToolsFunction.backError(403);
        }
        Map datas=ToolsFunction.StringToMap(orbody);
        if (!ToolsFunction.judgeDataLegal(datas,new String[]{"id","text","time","part"})){
            return ToolsFunction.backError(400);
        }
        datas.put("user",userData.getUser());
        if (ToolsFunction.isHasBadWord(datas.get("text").toString())){
            return ToolsFunction.backError(400);
        }
        if (Integer.parseInt(datas.get("time").toString())==0){
            return ToolsFunction.backError(400);
        }
        datas.putIfAbsent("color","#ffffff");
        if (filmService.setDanmu(datas)){
            return ToolsFunction.backNoneDataSuceessMsg();
        }else{
            return ToolsFunction.backError(500);
        }
    }

    //提交违规弹幕信息
    @GetMapping("/badDanmuFind")
    public Map badDanmuBan(@RequestParam("id") int id,
                           @RequestParam("danmuID") int danmuID,
                           @RequestParam("part") String part,
                           @RequestHeader("token") String token
    ){
        users userData=userService.getOneUserByToken(token);
        if (userData==null){
            return ToolsFunction.backError(403);
        }
        Map danmuData=filmService.getOneDanmu(id,danmuID,part);
        if (danmuData==null){
            return ToolsFunction.backError(400);
        }
        BadMsg badMsg=new BadMsg();
        badMsg.setFilmid(id);
        badMsg.setPart(part);
        badMsg.setDanmuid(danmuID);
        badMsg.setUser(userData.getUser());
        badMsg.setType(0);
        badMsg.setTime(ToolsFunction.getNowFormatTime());
        badMsg.setContent(danmuData.get("content").toString());
        badMsg.setBanuser(danmuData.get("user").toString());
        badMsgService.writeOneMsg(badMsg);
        return ToolsFunction.backNoneDataSuceessMsg();
    }
}
