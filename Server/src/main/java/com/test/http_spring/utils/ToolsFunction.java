package com.test.http_spring.utils;

import com.mysql.cj.xdevapi.JsonArray;
import com.sun.management.OperatingSystemMXBean;
import com.test.http_spring.pojo.film_data;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


//相关函数工具类
@Component
public class ToolsFunction {
    private static String appname=null;
    private static String tips=null;

    /**
     * 获取秒级时间戳
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @return 秒级时间戳
     */
    public static long getTimeStamp(){
        return new Date().getTime()/1000;
    }
    /**
     * 获取一个验证码邮件信息
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param addr 邮箱地址
     * @param vcode 验证码
     * @return 邮件信息实体类
     */
    public static SimpleMailMessage createNewMail(String addr,String vcode) {
        try {
            SimpleMailMessage message =new SimpleMailMessage();
            Map datas= GlobalValue.BootSetting;
            Map data= GlobalValue.emailData;
            tips=data.get("tips")==null?"“CoolVideo 一款开源的视频app模板”":data.get("tips").toString();
            appname=data.get("app-name")==null?"CoolVideo":data.get("app-name").toString();
            message.setFrom(datas.get("mail-username").toString());
            message.setText("尊敬的用户,您好!\n您的验证码为:"+vcode+",该验证码10分钟内有效。——"+tips);
            message.setTo(addr);
            message.setSubject(appname);
            message.setSentDate(new Date());
            return message;
        }catch (Exception e){
            return null;
        }
    }
    public static void errorLog(String text)
    {
        System.out.println(colorFont("[%s][Error]".formatted(getNowFormatTime())+text,31));
        try {
            FileWriter fw=new FileWriter("error.log",true);
            fw.write("[%s][Error]".formatted(getNowFormatTime())+text+"\n");
            fw.close();
        }catch (IOException e){
            System.out.println(colorFont("[%s][Error]日志记录失败".formatted(getNowFormatTime()),31));
        }

    }
    public static void warningLog(String text){
        System.out.println(colorFont("[%s][Warning]".formatted(getNowFormatTime())+text,33));
        try {
            FileWriter fw=new FileWriter("runner.log",true);
            fw.write("[%s][Warning]".formatted(getNowFormatTime())+text+"\n");
            fw.close();
        }catch (IOException e){
            System.out.println(colorFont("[%s][Error]日志记录失败".formatted(getNowFormatTime()),31));
        }
    }
    public static void infoLog(String text){
        System.out.println("[%s][INFO]".formatted(getNowFormatTime())+text);
    }
    /**
     * 将字符串以指定颜色输出
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param text 输入字符串
     * @param code 颜色代号，
     * @return 修改后的字符串
     */
    public static String colorFont(String text,int code){
        return "\033["+code+"m"+text+"\033[0m";
    }
    /**
     * 判断是否为一个邮箱地址
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param email 邮箱地址
     * @return 内存占用的百分比
     */
    public static boolean isEmail(String email) {
        if (email == null || email.length() < 1 || email.length() > 256) {
            return false;
        }
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(email).matches();
    }
    /**
     * 返回一个错误信息的载体
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param msg 错误信息原因
     * @return 错误信息载体
     */
    public static HashMap backErrorMsg(String msg){
        HashMap back=new HashMap();
        back.put("error",1);
        back.put("msg",msg);
        return back;
    }
    /**
     * 返回一个json类型的数据载体
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param data map数据
     * @return 信息载体
     */
    public static Map backSuccessDataMap(Map data){
        HashMap datas=new HashMap();
        datas.put("data",data);
        datas.put("error",0);
        return datas;
    }
    /**
     * 返回一个列表类型的信息载体
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param data 信息列表
     * @return 信息载体
     */
    public static HashMap backSuccessDataList(List data){
        HashMap datas=new HashMap();
        datas.put("data",data);
        datas.put("error",0);
        return datas;
    }
    /**
     * 返回一个电影信息的数据载体
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @return 数据载体
     * @param data 视频数据的实体类
     */
    public static HashMap backSuccessDataFilmData(film_data data){
        HashMap datas=new HashMap();
        datas.put("data",data);
        datas.put("error",0);
        return datas;
    }
    /**
     * 返回一个没有数据的成功载体信息
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @return 数据载体map
     */
    public static HashMap backNoneDataSuceessMsg(){
        HashMap back=new HashMap();
        back.put("error",0);
        return back;
    }
    /**
     * 获取当前系统CPU的负载情况
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @return 占用的百分比
     */
    public static double getCpuUsage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 系统CPU负载，返回0.0到1.0之间的值
        return osBean.getCpuLoad()*100;
    }


    /**
     * 将一个电影的数据转成pojo实体里
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param data 需要判断的map值
     */
    public static film_data writedata(Map data) {
        film_data f_data=new film_data();
        if (data.get("id")==null){
            f_data.setId(null);
        }else{
            f_data.setId(Integer.parseInt(data.get("id").toString()));
        }
        f_data.setName(data.get("name").toString());
        f_data.setLeader(data.get("leader").toString());
        f_data.setActor(data.get("actor").toString());
        f_data.setState(data.get("state").toString());
        f_data.setImg_url(data.get("img_url").toString());
        return f_data;
    }
    public static Map backError(int code){
        if (code==404){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "");
        }if (code==400){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }if (code==403){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Resource not found");
        }else{
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Resource not found");
        }
    }


    /**
     * 判断数据是否合法，合法返回Ture，反之返回false
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param data 需要判断的map值
     * @param check 对比的字符串数组
     */
    public static Boolean judgeDataLegal(Map data,String[] check){
        if (data==null){
            return false;
        }
        for (String key:check){
            if (data.get(key)==null||data.get(key).toString().contains("'")||data.get(key).toString().contains("--")){
                return false;
            }
        }
        try {
            new JSONObject(data);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    /**
     * 获取当前的物理内存的占用比
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @return 内存占用的百分比
     */
    public static double getPhysicalMemoryUsage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long totalPhysicalMemorySize = osBean.getTotalMemorySize();
        long freePhysicalMemorySize = osBean.getFreeMemorySize();
        return (double) (totalPhysicalMemorySize - freePhysicalMemorySize) / totalPhysicalMemorySize * 100;
    }
    /**
     * 获取当前的物理储存的占用比
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @return 储存的占用比
     */
    public static double getStorageUsage() {
        File file = new File("/");
        long totalSpace = file.getTotalSpace(); // 总空间，单位为字节
        long usableSpace = file.getUsableSpace(); // 可用空间，单位为字节
        long usedSpace = totalSpace - usableSpace; // 已用空间，单位为字节
        return (double) usedSpace / totalSpace * 100;
    }
    /**
     * 解析数据函数，可以将一个字符串json转化为map的格式
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param data 字符串json
     * @return 转化的数据，出错返回null
     */
    public static Map StringToMap(String data){
        Map<String, Object> map = new HashMap<>();
        try {
            JSONObject jsonObject=new JSONObject(data);
            for (String key : jsonObject.keySet()) {
                map.put(key, jsonObject.get(key));
            }
            return map;
        }catch (JSONException e){
            return null;
        }
    }
    public static String encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }
    public static String decode(String input) {
        byte[] decodedBytes = Base64.getDecoder().decode(input);
        return new String(decodedBytes);
    }
    /**
     * 获取一个字符串的md5
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param input 输入的字符串
     * @return md5值
     */
    public static String MD5(String input) {
        try {
            // 创建MessageDigest实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 生成输入字符串的MD5散列
            byte[] messageDigest = md.digest(input.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }
    public static Map JSONToMap(JSONObject json){
        Map<String, Object> map = new HashMap<>();
        for (String key : json.keySet()) {
            map.put(key, json.get(key));
        }
        return map;
    }

    /**
     * 将json数组实体转化为List
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param jsonArray json数组实体
     * @return 转化后的List
     */
    public static List<String> convertJsonArrayToList(JSONArray jsonArray) {
        List list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (!(jsonArray.get(i)==null)){
                    if (jsonArray.get(i) instanceof String){
                        if (String.valueOf(jsonArray.get(i)).isEmpty()){
                            continue;
                        }
                        list.add(jsonArray.get(i));
                        continue;
                    }
                    if (jsonArray.get(i) instanceof Integer){
                        list.add(String.valueOf(jsonArray.get(i)));
                        continue;
                    }
                    if (jsonArray.get(i) instanceof JSONObject){
                        list.add(JSONToMap((JSONObject) jsonArray.get(i)));
                        continue;
                    }
                    list.add(jsonArray.get(i));
                }
            } catch (Exception e) {
                return null;
            }
        }
        return list;
    }

    /**
     * 将图片数据压缩后转化为base64编码
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param imageData 图片的数据
     * @param targetWidth 压缩后的宽度
     * @param targetHeight 压缩后的高度
     * @return Base64编码
     */
    public static String compressAndEncodeImage(byte[] imageData, int targetWidth, int targetHeight) throws IOException {
        // 将字节数组转换为BufferedImage
        ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        BufferedImage originalImage = ImageIO.read(bis);
        bis.close();

        // 计算压缩比例
        double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
        int newWidth = targetWidth;
        int newHeight = targetHeight;

        if (originalImage.getWidth() > originalImage.getHeight()) {
            newHeight = (int) (targetWidth / aspectRatio);
        } else {
            newWidth = (int) (targetHeight * aspectRatio);
        }

        // 创建压缩后的图片缓冲区
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // 绘制调整大小后的图像
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        // 将BufferedImage转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        byte[] compressedImageData = baos.toByteArray();
        baos.close();

        return Base64.getEncoder().encodeToString(compressedImageData);
    }
    /**
     * 爬虫解析，用于获取视频的播放地址,可以替换成自己的cdn解析逻辑
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param id 目标视频的id
     * @param number 目标视频的选集
     * @return 目标视频的地址
     */
    public static String get_video_url(String id, String number){
        try {
            HttpClient client = HttpClient.newHttpClient();
            // First request
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.mutean.com/voddetail/" + id + ".html"))
                    .headers("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .build();

            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            Document doc1 = Jsoup.parse(response1.body());
            if (doc1.title().equals("错误提示 - MuteFun动漫网站-无声乐趣-(゜-゜)つロ 干杯~")){
                return "error";
            }
            Element mainBtn = doc1.select("a.main-btn").first();
            int cdn = Integer.parseInt(mainBtn.attr("href").split("-")[1]) + 1;

            // Second request
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.mutean.com/vodplay/" + id + "-" + cdn + "-" + number + ".html"))
                    .headers("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .build();

            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            Document doc2 = Jsoup.parse(response2.body());
            Elements playerBoxMain = doc2.select("div.player-box-main");
            String playerDataScript = playerBoxMain.html().split("player_aaaa=")[1].split("</script>")[0];
            JSONObject playerData = new JSONObject(playerDataScript);
            String firstUrl = playerData.getString("url");

            // Third request
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.mutean.com/addons/dp/player/dp.php?key=0&from=m2&id=" + id + "&api=&url=" + firstUrl + "&jump="))
                    .headers("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .build();

            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            Document doc3 = Jsoup.parse(response3.body());
            Elements scripts = doc3.select("script");
            String configScript = scripts.get(3).html().split("var config = ")[1].split("\n" +"        if")[0];
            JSONObject config = new JSONObject(configScript);
            return config.getString("url");
        }catch (IndexOutOfBoundsException | IOException | InterruptedException e1){
            return "error";
        }
    }
    public static String getNowFormatTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(new Date());
    }


    /**
     * 获取格式化的日期
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @return 格式化时间
     */
    public static String getNowFormatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * 加密登录信息使用
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param data 需要加密的字符串
     * @return 加密字符串
     */
    public static String encodeData(String data) {
        String key = getNowFormatDate();
        int length = key.length() - 1;
        int dataLength = data.length();
        int count = 0;
        StringBuilder last = new StringBuilder();
        for (int i = 0; i < dataLength; i++) {
            char b = data.charAt(i);
            if (count == length) {
                count = 0;
            }
            b = (char) (b - (key.charAt(count) % 10) + 3);
            last.append(b);
            count++;
        }

        return last.toString();
    }

    /**
     * 解密登录信息使用
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param data 加密信息
     * @return 解密后的字符串
     */
    public static String decodeData(String data) {
        String key = getNowFormatDate();
        int length = key.length() - 1;
        int dataLength = data.length();
        int count = 0;
        StringBuilder last = new StringBuilder();
        for (int i = 0; i < dataLength; i++) {
            char b = data.charAt(i);
            if (count == length) {
                count = 0;
            }
            b = (char) (b + (key.charAt(count) % 10) - 3);
            last.append(b);
            count++;
        }

        return last.toString();
    }



    /**
     * 检查链接的有效性,时间判断，为4个消失有效时间计算
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param url 判断的链接
     * @return 是否有效，主要看是否超时
     */
    public static boolean checkUrlState(String url){
        if (getTimeStamp()-Long.parseLong(url.split("&t=")[1])>14400){
            return false;
        }else {
            return true;
        }
    }


    /**
     * 获取本地所有的缓存数据
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @return 文件列表，数组包含Map对象，Map里一个id，一个num集数
     */
    public static List<Map<String,String>> getLocalCdnData(){
        String[] filenames=new File("video/").list();
        ArrayList datas=new ArrayList();
        if (filenames != null) {
            for (String id:filenames){
                File oneVideo=new File("video/"+id);
                if (oneVideo.list()==null){
                    continue;
                }
                for (String num: oneVideo.list()){
                    HashMap<String,String> oneData=new HashMap<>();
                    oneData.put("id",id);
                    oneData.put("num",num.replace(".mp4",""));
                    datas.add(oneData);
                }
            }
            return datas;
        }
        return null;
    }


    /**
     * 用于获取用户设置的函数
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param data 字符串json
     * @param key 需要获取的值
     * @return 返回数组
     */
    public static List getJsonListData(String data,String key){
        try {
            JSONObject usersetting=new JSONObject(data);
            if (!judgeDataLegal(StringToMap(data),new String[]{key})){
                return null;
            }
            return convertJsonArrayToList((JSONArray) usersetting.get(key));
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
    /**
     * 获取写入数据后的对象
     * @author Conyafer
     * &#064;Time   2024-8-7
     * @param data 字符串json，原始数据
     * @param key 键名
     * @param newData 新的数据，列表
     * @return 新数据的json对象
     */
    public static JSONObject setJsonData(String data,String key,List newData){
        JSONObject jsondata=new JSONObject(data);
        jsondata.put(key,newData);
        return jsondata;
    }
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            ".*\\b(SELECT|INSERT|UPDATE|DELETE|UNION|AND|OR|DROP|--|#|\\*\\/|\\/\\*|\\bHAVING)\\b.*",
            Pattern.CASE_INSENSITIVE);

    public static boolean isSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).matches();
    }
    public static String getNumber(String text){
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher=pattern.matcher(text);
        return matcher.replaceAll("");
    }

    /**
     将资源写入到临时文件夹

     **/
    public static Path writeDataToTem(String file){
        try {
            ClassPathResource classPathResource = new ClassPathResource(file);
            //保存临时目录
//            Path tempDir = Files.createTempDirectory("extractedZip");

            //保存一个实体目录
            File tempFile=new File("autoGetter");
            if (!tempFile.exists()){
                if (!tempFile.mkdir()){
                    return null;
                }
            }
            Path tempDir=tempFile.toPath();
            InputStream zipInput=classPathResource.getInputStream();
            File zipFile=new File(tempDir+"/test.zip");
            OutputStream zipFileout=new FileOutputStream(zipFile);
            byte[] zipbuffer=new byte[1024];
            int size;
            while ((size=zipInput.read(zipbuffer))!=-1){
                zipFileout.write(zipbuffer,0,size);
            }
            zipFileout.close();
            zipInput.close();
            try (ZipFile zip = new ZipFile(zipFile)) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    Path entryPath = tempDir.resolve(entry.getName());
                    if (entry.isDirectory()) {
                        // 如果条目是目录，创建目录
                        Files.createDirectories(entryPath);
                    } else {
                        // 如果条目是文件，创建文件并写入内容
                        Files.createDirectories(entryPath.getParent());
                        try (InputStream in = zip.getInputStream(entry);
                             OutputStream out = Files.newOutputStream(entryPath)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = in.read(buffer)) > 0) {
                                out.write(buffer, 0, len);
                            }
                        }
                    }
                }
            }
            return tempDir;
        }catch (IOException e){
            System.out.println(e);
            return null;
        }

    }


    public static String getSysType(){
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return "win";
        } else if (osName.contains("nix") || osName.contains("nux")) {
            return "linux";
        } else {
            return "unsupport";
        }
    }

    public static void terminateGetter(){
        if (GlobalValue.Getter!=null){
            GlobalValue.autoGetterState="stopping";
            GlobalValue.Getter.destroy();
        }
    }

    public static String getSysDetailType(){
        return System.getProperty("os.name").toLowerCase();
    }

    public static boolean isHasBadWord(String data){
        if (data.length()>128){
            return false;
        }
        if (GlobalValue.badWords.isEmpty()){
            return false;
        }
        for (String item:GlobalValue.badWords){
            if (data.contains(item)){
                return true;
            }
        }
        return false;
    }
    public static boolean changeBadWords(String word){
        try {
            // 以追加模式打开文件，并写入内容
            Files.write(Paths.get("badWord.txt"), word.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            GlobalValue.badWords.clear();
            if (!word.isEmpty()){
                GlobalValue.badWords.addAll(Arrays.asList(word.split(",")));
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
