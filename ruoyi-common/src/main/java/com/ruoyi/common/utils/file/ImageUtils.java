package com.ruoyi.common.utils.file;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 图片处理工具类
 *
 * @author ruoyi
 */
public class ImageUtils
{
    private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

    public static byte[] getImage(String imagePath)
    {
        InputStream is = getFile(imagePath);
        try
        {
            return IOUtils.toByteArray(is);
        }
        catch (Exception e)
        {
            log.error("图片加载异常 {}", e);
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
    }

    public static InputStream getFile(String imagePath)
    {
        try
        {
            byte[] result = readFile(imagePath);
            result = Arrays.copyOf(result, result.length);
            return new ByteArrayInputStream(result);
        }
        catch (Exception e)
        {
            log.error("获取图片异常 {}", e);
        }
        return null;
    }

    /**
     * 读取文件为字节数据
     *
     * @param url 地址
     * @return 字节数据
     */
    public static byte[] readFile(String url)
    {
        InputStream in = null;
        try
        {
            if (url.startsWith("http"))
            {
                // 网络地址
                URL urlObj = new URL(url);
                URLConnection urlConnection = urlObj.openConnection();
                urlConnection.setConnectTimeout(30 * 1000);
                urlConnection.setReadTimeout(60 * 1000);
                urlConnection.setDoInput(true);
                in = urlConnection.getInputStream();
            }
            else
            {
                // 本机地址
                String localPath = RuoYiConfig.getProfile();
                String downloadPath = localPath + StringUtils.substringAfter(url, Constants.RESOURCE_PREFIX);
                in = new FileInputStream(downloadPath);
            }
            return IOUtils.toByteArray(in);
        }
        catch (Exception e)
        {
            log.error("获取文件路径异常 {}", e);
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }


    /**
     * 缓存图片
     * @param url 图片URL
     * @return 是否成功
     */
    public static Boolean cacheImage(String url){

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/114.0.0.0 Safari/537.36");
        HttpEntity<Void> reqEntity = new HttpEntity<>(headers);


        ResponseEntity<byte[]> entity;
        try{
            // 使用 RestTemplate 获取远程图片
            RestTemplate restTemplate = new RestTemplate();
            entity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    reqEntity,
                    byte[].class
            );
        }catch (Exception e){
            return false;
        }


        // 写入图片
        return writeImage(entity.getBody(),url);

    }

    /**
     * 写入图片
     * @param imageURL 图片路径
     * @param imageData 图片数据
     * @return 是否成功
     */
    public static Boolean writeImage(byte[] imageData,String imageURL)
    {
        try
        {
            String ImgID=StringUtils.getURLFileName(imageURL);
            // 写入图片
            Path path = Paths.get("imgCache/"+ImgID);
            Files.write(path, imageData);
            return true;
        }
        catch (Exception e)
        {
            log.error("写入图片异常 {}", e.getMessage());
            return false;
        }
    }


    /**
     * 获取缓存的图片
     * @param url 图片URL
     * @return 图片数据
     */
    public static byte[] getCacheImage(String url){
        //获取图片的ID值
        String imgID=StringUtils.getURLFileName(url);

        Path path = Paths.get("imgCache/"+imgID);
        try {
            return Files.readAllBytes(path);
        }catch (IOException e){
            return null;
        }
    }


    /**
     * 获取图片的HttpHeaders
     * @param size 图片数据
     * @param url 图片URL
     * @return HttpHeaders
     */
    public static HttpHeaders getImgHeaders(String url, int size){
        String type=StringUtils.getURLFileName(url).split("\\.")[1];
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // 设置内容类型为JPEG
        headers.setContentLength(size); // 设置文件大小
        return headers;
    }

}
