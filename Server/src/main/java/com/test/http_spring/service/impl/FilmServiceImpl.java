package com.test.http_spring.service.impl;

import com.test.http_spring.mapper.filmMapper;
import com.test.http_spring.pojo.film_data;
import com.test.http_spring.service.FilmService;
import com.test.http_spring.utils.GlobalValue;
import com.test.http_spring.utils.ToolsFunction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class FilmServiceImpl implements FilmService {


    @Autowired
    private filmMapper fm;

    @Override
    public List<film_data> findAllFilm() {
        return fm.findAllFilm();
    }

    @Override
    public List<film_data> findFilmLikeByName(String info) {
        return fm.findFilmLikeByName(info);
    }

    @Override
    public film_data findFilmDataById(int id) {
        return fm.findFilmDataById(id);
    }

    @Override
    public void addOneFilmAgree(int id) {
        fm.addOneFilmAgree(id);
    }
    public void removeOneFilmAgree(int id){
        fm.removeOneFilmAgree(id);
    }

    @Override
    public void removeOneFilm(int id) {
        fm.removeOneFilm(id);
    }

    @Override
    public void addOneFilm(film_data data) {
        fm.addOneFilm(data);
    }

    @Override
    public void updateData(film_data data) {
        fm.updateData(data);
    }




    //异步启动爬虫程序
    //终止方式信号使用方式，设置为stopping状态时,调用destroy方法才有用,否则爬虫程序会自动转化为retrying信号
    @Override
    @Async
    public void bootAutoGetter() {
        String pythonName;
        Path temscript;
        String appName;

        //启动检查阶段
        if (GlobalValue.autoGetterState.equals("running")){
            ToolsFunction.warningLog("自动程序正在重启..");
            GlobalValue.autoGetterState="stopping";
            GlobalValue.Getter.destroy();
            temscript=Paths.get(GlobalValue.temPath);
            appName=ToolsFunction.getSysType().equals("win")?"win":"linux";
        }else{
            if (ToolsFunction.getSysType().equals("win")){
                pythonName="pythonscript/test_win/test.zip";
                appName="test.exe";
            }else if (ToolsFunction.getSysType().equals("linux")){
                pythonName="pythonscript/test_linux/test.zip";
                appName="test";
            }else{
                ToolsFunction.errorLog("不支持的操作系统");
                GlobalValue.Getter=null;
                GlobalValue.autoGetterState="terminated";
                return;
            }
            temscript=ToolsFunction.writeDataToTem(pythonName);
            if (temscript==null){
                ToolsFunction.errorLog("启动失败，因为写入临时文件失败");
                GlobalValue.autoGetterState="terminated";
                return ;
            }
            GlobalValue.temPath=temscript.toString();
        }


        //如果为linux系统，更改文件目录的权限
        if (ToolsFunction.getSysType().equals("linux")){
            ProcessBuilder filePermission=new ProcessBuilder("sudo","chmod","-R","777","./");
            try {
                Process p=filePermission.start();
                p.waitFor();
            }catch (Exception e){
                ToolsFunction.warningLog("文件权限更改失败，自动程序可能不可用,E:"+e.getMessage());
            }
        }

        //设置启动信号
        GlobalValue.autoGetterState="booting";

        //设置启动的语句
        ProcessBuilder processBuilder;
        if (GlobalValue.BootSetting.get("database-type").toString().equals("mysql")){
            processBuilder=new ProcessBuilder(
                    temscript.toAbsolutePath()+"/"+appName,
                    "mysql",
                    GlobalValue.BootSetting.get("database-position").toString(),
                    GlobalValue.BootSetting.get("database-name").toString(),
                    GlobalValue.BootSetting.get("database-username").toString(),
                    GlobalValue.BootSetting.get("database-password").toString()
            );
        }else{
            String databaseAddr=new File(GlobalValue.BootSetting.get("database-position").toString()).getAbsolutePath();
            processBuilder=new ProcessBuilder(
                    temscript.toAbsolutePath()+"/"+appName,
                    "sqlite",
                    databaseAddr,
                    GlobalValue.BootSetting.get("database-name").toString(),
                    GlobalValue.BootSetting.get("database-username").toString(),
                    GlobalValue.BootSetting.get("database-password").toString()
            );
        }

        GlobalValue.getterLogs.clear();
        GlobalValue.getterLogs.add("[%s] run service in path:%s".formatted(
                ToolsFunction.getNowFormatTime(),
                temscript
        ));
        processBuilder.redirectErrorStream(true);
        int tryTimes=0;
        while (tryTimes<5) {
            if (GlobalValue.autoGetterState.equals("stopping")){
                break;
            }
            try {
                Process process = processBuilder.start();
                GlobalValue.Getter=process;
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("running...")) {
                        //检测到stopping终止信号，跳出循环
                        if (GlobalValue.autoGetterState.equals("stopping")){
                            break;
                        }
                        //设置为运行信号
                        GlobalValue.autoGetterState="running";
                        GlobalValue.getterLogs.add("[%s] 爬虫启动成功".formatted(
                                ToolsFunction.getNowFormatTime()
                        ));
                        ToolsFunction.infoLog("自动程序启动成功");
                    }else{
                        if (GlobalValue.getterLogs.size()>100){
                            GlobalValue.getterLogs.remove(0);
                        }
                        GlobalValue.getterLogs.add("["+ToolsFunction.getNowFormatTime()+"] "+line);
                    }
                }
                //发现是终止信号，跳出循环，停止重试
                if (!GlobalValue.autoGetterState.equals("stopping")){
                    tryTimes++;
                    GlobalValue.autoGetterState="retrying";
                    ToolsFunction.errorLog("自动程序运行出错,正在重启.... "+tryTimes+"/5");
                    GlobalValue.getterLogs.add("[%s] 爬虫运行出错，正在重启.... %d/5".formatted(
                            ToolsFunction.getNowFormatTime(),
                            tryTimes
                    ));
                }else{
                    break;
                }
                //间歇3秒
                Thread.sleep(3000);
            } catch (Exception e) {
                if (!GlobalValue.autoGetterState.equals("stopping")){
                    tryTimes++;
                    GlobalValue.autoGetterState="retrying";
                    ToolsFunction.errorLog("自动程序启动失败，正在重试"+tryTimes+"/5");
                    GlobalValue.getterLogs.add("[%s] 爬虫运行出错，正在重启.... %d/5,原因:".formatted(
                            ToolsFunction.getNowFormatTime(),
                            tryTimes
                    )+e.getMessage());
                    //间歇3秒
                    try {
                        Thread.sleep(3000);
                    }catch (Exception e1){
                        continue;
                    }
                }else{
                    break;
                }
            }
        }
        //设置为终止状态
        GlobalValue.autoGetterState="terminated";
        if (tryTimes!=5){//用户主动停止
            ToolsFunction.infoLog("自动程序已停止运行");
            GlobalValue.getterLogs.add("[%s] 爬虫程序已停止".formatted(
                    ToolsFunction.getNowFormatTime()
            ));
        }else{//超过最大尝试次数
            ToolsFunction.errorLog("自动程序已达最大尝试次数，请稍后重试或手动启动");
            GlobalValue.getterLogs.add("[%s] 爬虫程序已达最大尝试次数，请稍后重试".formatted(
                    ToolsFunction.getNowFormatTime()
            ));
        }
    }

    @Override
    public JSONArray getDanmu(int id,String part) {
        String ordata=fm.getDanmu(id);
        if (ordata==null){
            return null;
        }
        JSONObject jsonObject=new JSONObject(ordata);
        try {
            return jsonObject.getJSONArray(part);
        }catch (Exception e){
            return new JSONArray();
        }
    }

    @Override
    public boolean DeleteDanmu(int id,String part,int danmuID) {
        JSONObject jsonObject=new JSONObject(fm.getDanmu(id));
        if (!jsonObject.has(part)){
            return false;
        }
        JSONArray jsonArray=new JSONArray(jsonObject.getJSONArray(part));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject one = jsonArray.getJSONObject(i);
            if (Integer.parseInt(one.get("id").toString())==danmuID){
                jsonArray.remove(i);
                jsonObject.put(part,jsonArray);
                fm.setDanmu(id,jsonObject.toString());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setDanmu(Map data){
        Integer id=Integer.parseInt(data.get("id").toString());
        String part=data.get("part").toString();
        film_data fData=fm.findFilmDataById(id);
        String datas=fData.getDanmu();
        if (datas==null){
            return false;
        }
        if (Integer.parseInt(part)<=0||Integer.parseInt(ToolsFunction.getNumber(fData.getState()))<Integer.parseInt(part)){
            return false;
        }
        JSONObject jsonObject=new JSONObject(datas);
        if (!jsonObject.has(part)){
            jsonObject.put(part,new JSONArray());
        }
        JSONArray jsonArray=jsonObject.getJSONArray(part);
        if (jsonArray.isEmpty()){
            data.put("id",1);
        }else{
            data.put("id",Integer.parseInt(jsonArray.getJSONObject(jsonArray.length()-1).get("id").toString())+1);
        }
        jsonArray.put(data);
        fm.setDanmu(id,jsonObject.toString());
        return true;
    }

    @Override
    public Map getOneDanmu(int filmid, int danmuID,String part) {
        String danmudata=fm.getDanmu(filmid);
        if (danmudata==null){
            return null;
        }
        JSONObject jsonObject=new JSONObject(danmudata);
        if (!jsonObject.has(part)){
            return null;
        }
        JSONArray jsonArray=jsonObject.getJSONArray(part);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject one = jsonArray.getJSONObject(i);
            if (Integer.parseInt(one.get("id").toString())==danmuID){
                HashMap back=new HashMap();
                back.put("content",one.get("text").toString());
                back.put("user",one.get("user").toString());
                return back;
            }
        }
        return null;
    }
}
