package com.test.http_spring.utils;

import org.springframework.stereotype.Component;

import java.util.*;


//用于产生全全局静态变量使用
@Component
public class GlobalValue {
        //测试用路径
         public static final String randomAdminPath ="admin";
        //随机后台路径
//        public static final String randomAdminPath = ToolsFunction.MD5(UUID.randomUUID().toString()).substring(0, 8);
        //子界面的路由注册，非注册的路由会被返回404

        //顺序，页面名称，引入js文件的名称，获取数据的api名称，删除数据api的名称，添加数据api的界面名称，修改数据api的名称,添加数据弹窗标题，界面的标题
        public static final Map<String,ArrayList<String>> childPageNameList=new HashMap<>(){{
            put("welcome",new ArrayList<>(Arrays.asList("","","","","","","")));
            //控制器界面
            put("user-control",new ArrayList<>(Arrays.asList("userFunctions.js","getAllUser","removeUser","user-add","updateUser","添加一个用户","用户管理")));
            put("cdn-control",new ArrayList<>(Arrays.asList("cdnFunctions.js","getCdnData","removeCdnData","cdn-add","","添加一个缓存信息","本地缓存管理")));
            put("resource-control",new ArrayList<>(Arrays.asList("resourceFunctions.js","getAllVideo","removeData","resource-add","updateData","添加一条数据","视频数据管理")));
            put("banner-control",new ArrayList<>(Arrays.asList("bannerFunctions.js","getAllBanner","removeBanner","banner-add","updateBanner","添加一条Banner","Banner管理")));
            put("fastfunction-control",new ArrayList<>(Arrays.asList("fastFunctions.js","getAllFastFunctions","removeFastFunction","fastfunction-add","updateFastFunction","添加一个快速功能","快速功能管理")));
            put("danmu-control",new ArrayList<>(Arrays.asList("danmu.js","getAllBadMsg","removeBadMsg","badword-add","danmu","新建一个弹幕拦截关键词","违规弹幕信息管理")));
            //添加数据的子界面
            put("resource-add",new ArrayList<>(Arrays.asList("","","","","addData","","")));
            put("user-add",new ArrayList<>(Arrays.asList("","","","","addUser","","")));
            put("banner-add",new ArrayList<>(Arrays.asList("","","","","addBanner","","")));
            put("fastfunction-add",new ArrayList<>(Arrays.asList("","","","","addFastFunction","","")));
            put("cdn-add",new ArrayList<>(Arrays.asList("","","","","","","")));
            put("badword-add",new ArrayList<>(Arrays.asList("","getAllBadWords","","","addBadWord","","")));
        }};
        public static Map emailData;
        public static Float speedLimite;
        public static Map BootSetting;
        public static String version="V1.26";
        public static boolean canUserCdn=true;
        //爬虫程序的状态
        //一共五个状态
        //terminated 停止的情况，程序没有在运行
        //booting 程序正在启动中
        //stopping 程序正在停止中
        //running 程序正在运行中
        //retrying 程序正在错误重启中
        public static String autoGetterState="terminated";
        //爬虫程序的进程变量
        public static Process Getter;
        //临时目录的位置
        public static List<String> getterLogs=new ArrayList<>();
        public static String temPath=null;
        public static List<String> badWords=new ArrayList<>();
        //默认头像的bbase64编码
        public static String icon="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAACXBIWXMAAA7EAAAOxAGVKw4bAAAJyklEQVR4Xu2dfYwcZR3Hv7/ZvRJTTKWJEnlLCBgqkBBNW94UiAKhRAwGzu78Zg80AXkrYCFAAQ3FgEVQJGjAgG9w+zxzdVFjIFSChogvhfZiYkINL5L+cYAhpFWgUenezs8/brfd+3V2dvdu55m53Hz+2v38dm/n5rsz+8wzzzxDWGAw820A7ta+C1s9z1tXq9X+qgt5hbTIG9Vq9dNRFN0FYI2uDUgE4DZr7Xd0IU/kNpDR0dHSyMjITgBH6tp8EZEbwzC8X/s8kMtAmPlJAF/QftgQ0dnGmN9rnyW5CqRSqZzsed4L2qcJEb1ojDlF+6zITSDMvBHAHdq7wlqbi3WRi4UIguAFETlZ+wR2EtGNe/fu/UO9Xt+ti2NjYydGUbRORK7QtSTyEErmC8DMUwCO0D6GHSLy+TAM39aFXgRBcKWIPKx9HFmHkumHM/N6AL1aO3vK5fKKxx9//E1dGJQgCDaJyAbtNVmGktkHB0Fwuoj8SXvF9dbaB7WcL8ws2mmyCiWTDx0dHT14ZGTkfe07IaIjjTFvaD8smPlVAJ/QvoPnrLWf0zJtMgmk1zfU1bezV8sui+MUJ/94J8wcAKhp38ZVGG2YeSuArschrpfH6YcByVtHFEWnT0xM/EX7tElaJtfdLE4DCYLgXhG5SfsWP7LWXqWlK5JCcbmVlLVIk4QwkGUYAEBE93RrEgdBcKsxZpP2aeAs+Wq1+vEoit7SHgBE5NYwDO/R3jUJW0lkrS1pmQbOthARuVe7NnkIAwCI6KouR/SeFmnhbAtJ+Pb9zlp7jpZZ0W05ReS0MAy3aj9snG0hCdyuRR4hogcADNIBOiecBDI2NnZ0s9nUGgBgrd2mXcb8DMBXtQSwWos0cBJIs9lcqV1eKZVK9zebzbhAnOAkEBFZSRT7c/VfLbJmfHz8JWbW2hlOAiGiVdq12K7FYsdJIAC67bImtVjsuArkLQDHaQngMC0WO04CIaLtIhIXSLdd2aLFSSAiMgmgqj2AY7TImtHR0eXaucRJIEQ0KRJ7AJw7lixZcmaWy+okkN27d08ecsghWucSEfmedi5xEsiWLVs+6Na2Z+b11trva58hR2vRwkkXj5NAWkwhfuD0JgC5CMT3/UO1a2Ot/bZ2aeAykBsB/EJLAAeNjo4eXK/X9+iCa4jI6YCGOJwFYq2td9ttlcvll9Hf6MXUuOSSSw6fnp4+QfsWW7RIC2eBtNgLYImWRHR4EATHG2P+rmuumJ6eflm7Np7nfUO7tHAaSBRFR3ueFzskVER2wOEJs06Y+ToAB2vfYsrlJXHOVwAzb0P3I/T3rLXLtEyTIAiOEJEp7ds0Go1yvV6PP5mTAs4DAbqfJm3hdAhnj2V5ylp7gZZpklUgjwK4TPs2RFQzxoxpP2x6hOF0PFYb5x/Yhpl3AUjqN3rHWvsxLYdBpVI5zfO8P2vfSRRFp0xMTLyofdpkFgjQ+xsKAER0wjBbX8z8MIArtVdstNbeqaULMg0E6C8UEXlzenp6xXwOHn3f30BEPUcfZn0RaOaBAP2F0mKviNwdhuG3dCEOZl6NmVkfzta1LrxhrY3r3nFGLgIBBgpF8zpmzs2/hZlTxasAfGjWK/rjhjx0cuYmEADg/i8AHSpE9BljTOKPvCtyFQjQu0k8bBqNxofn89s0bHIXSJseR/TDoGqtNVpmTW4DAYBKpXKY53k7EdMhOVeI6D5jzM3a54VcB9IJM38FMy2mgYcOEVGNiG6u1Wr/1LW8sWAC6WTNmjUHLV++fGVriOpKEVmFmaAmRWQ7EU2WSqXJ8fHxnfq9BQUFBQUFBQUFi5EF0exl5tVEtEpEVmPmWr8V+jVdeB/ANhHZJiLbm83mtnq9HjvIIi/kLhDf988houuQ/qyk7wB4kIgeNMa8p4tZkXkg1Wr1uCiKvgkg0DXH7BaRW8Iw/LEuuCSTQNauXXtMqVQaB3CqruWIm6y139UybZwG0u+chznj1SiKgomJCSfXQzoJJAiCX4nIl7RfaIjIF8MwfFL7YZJqIHOYj7cXrwF4XkT+6HneK57nvdtoNN4dGRl5d2pq6oOjjjpq2fT09LIoipYBWEZEZxDRZwGcgSF24QO4zFr7Ey2HQSqBMPNDAOYz/9V/ANzved5jtVrtH7o4X4IguFhELsU8W3LNZvPYzZs3v679fBhqIL7vf5mINmvfJ7/0PO/2Wq32ii6kDTNfhZlzLXO57m6btXZoe4GhBcLMrwE4Vvse/CaKoqsnJiZiJzbLgiAILheRhzDglQFEFBhjrPaDMu9AfN+/iIie0D4JEeEwDEPt8wYzPwPgXO0T2GGtPVHLQZhXIMz8LPofhJar4TaDMOhImEajccRcu2jmHAgz7wGwVPs4iOh8Y4yzy8LSgpmfRp+3XhKRc8MwfFb7XswpkH5HGYrI+jAMH9B+ocPM/wLwEe01c/n/Bw6kzzD+ba2dS4tlweD7/teJqOfQUxlwIuaBAukzjA0253dCGyb9rBMiGjPG1LSPo+9A+vzg1caYRTcpGfc3yvI8a+0zWmr6CqSfMLK4/CtPMPM9AG7RvpN+xhH3PPjxff857TSLPQwAsNZuYOa3kXDHoNY9UxLXVWKRma8HkNhKKMKYTaVS+ZTneYnXtSets66Fa6+99qBdu3b9T/tOkv7wYqY1SDzpwLBr/1fXFcrMDSTs0oowkgmC4FIR+bn2baTL1OWxK5WZ1wH4gfZtijD6g5lrSBgrELceDxBAz1ZVLi90yStJ65KIHjXGfK3THbBLYubHtOtgRxHGYJRKpUObzWbszTBF5HIAswI5YAtJSjRuEyvoDTP/EMA12gMAEf3WGLOvw3LWFsLMv+583omIrNeuoD+steuYOTYQETmv87neZV2onu9j0F7LgtkQ0QUiEjtihZmfsNZeDHQEUq1Wr4iiaP+rZnOmFgWDYYx5irtMcQjgovaDfYFEUfSj9mONtfZ57QoGh4jOF5GntQcA3/dPDcNwq95lHQAR3a1dwdwwxmzptpUQ0U8BfLIMAEEQXCNdptc2xjibAHIxQET3Sfz9HFcArV2WiFw9u1aQFkuXLr19z549cYEA2P8bcvwsu5+NWhTMj0ceeaTRbbfl+/6Fib8hNqNZ1RYBbwOIm9b8+sRAClLDALhBSyI6q1ypVM7ShYJ0EZFNRHRAIABQ9jzvLC0BQET+xsx3YH9/l3Q81iTV4hj09cDs9+j36+cLlrKInEkx9xgkopMAnKR9QbqUS6XS5VEUvaYLBdlAQHKXe4FTNrYPDD9KRO/oaoFTdlhr79z34xEEwSkicsBJ9wInbLbWVoCYlonv+xcSUQXAWl0rGB5E9JKIbLbW3tXp/w9Rh5aZTD/I+gAAAABJRU5ErkJggg==";

}
