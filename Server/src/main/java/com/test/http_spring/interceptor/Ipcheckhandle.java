package com.test.http_spring.interceptor;

import com.test.http_spring.utils.GlobalValue;
import com.test.http_spring.utils.ToolsFunction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Ipcheckhandle implements HandlerInterceptor {
    private ConcurrentHashMap<String,long[]> ipLogs = new ConcurrentHashMap<>();
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int times;//每个ip的次数
    private final int timeOutTime=60;//计算周期，默认60秒钟
    public Ipcheckhandle(){
        times=(int)(60* GlobalValue.speedLimite);
        ToolsFunction.infoLog("访问速率限制已开启");
        ToolsFunction.infoLog("访问速率限制为"+GlobalValue.speedLimite);
    }
    @Scheduled(fixedRate = 60000)
    public void clearDisableIpLogs(){
        long nowTime=ToolsFunction.getTimeStamp();
        ipLogs.entrySet().removeIf(entry -> nowTime - entry.getValue()[1] > timeOutTime);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //该拦截器默认的拦截条件是60秒内连续访问超过50次，可以在配置文件里自定义
        //检测使用，默认为直连模式，如果直连地址显示的是127.0.0.1，自动切换nginx代理模式,如果为空，则进入直连模式
        String ip=request.getRemoteHost();
        long nowTime=ToolsFunction.getTimeStamp();
        if (ip.equals("127.0.0.1")){
            if (request.getHeader("X-Real-IP")!=null){
                ip=request.getHeader("X-Real-IP");
            }
        }
        if (ipLogs.containsKey(ip)){
            long[] ipData=ipLogs.get(ip);
            if (ipData[0]<times && nowTime-ipData[1]<timeOutTime){
                ipLogs.get(ip)[0]+=1;
                return true;
            }
            if (nowTime-ipData[1]<timeOutTime && ipData[0]>=times){
                ipLogs.get(ip)[1]=nowTime;
                response.setStatus(403);
                HashMap data=new HashMap();
                data.put("msg","您的访问过于频繁，请稍后重试!");
                data.put("IP",ip);
                data.put("Time",formatter.format(new Date()));
                data.put("error",1);
                JSONObject dataJson=new JSONObject(data);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter pw= response.getWriter();
                pw.write(dataJson.toString());
                pw.flush();
                pw.close();
                return false;
            }
            //该判断条件用于特殊情况，当这个ip长时间没有访问时，且没有被循环清理掉的时候。当用户再次访问,重新注册这个ip地址
            if (nowTime-ipData[1]>timeOutTime && ipData[0]<times){
                ipLogs.get(ip)[1]=nowTime;
                ipLogs.get(ip)[0]=1;
                return true;
            }
        }
        long[] oneIp=new long[2];
        oneIp[0]=1;oneIp[1]=nowTime;
        ipLogs.put(ip,oneIp);
        return true;
    }
}
