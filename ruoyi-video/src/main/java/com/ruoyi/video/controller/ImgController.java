package com.ruoyi.video.controller;


import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.ImageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/img")
@Tag(name = "图片管理", description = "图片管理")
public class ImgController {

    @Anonymous
    @Operation(summary = "通过链接获取封面并缓存")
    @Parameter(name = "url", description = "图像地址")
    @GetMapping("/cover")
    public ResponseEntity<byte[]> getImg(
            @RequestParam("url") String url,
            HttpServletResponse response
    ){
        //如果url为空，返回404
        if (url==null || url.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        try {
            // 本地资源，本机下载
            if (!(url.startsWith("http")|| url.startsWith("https"))){
                // 本地资源路径
                String localPath = RuoYiConfig.getProfile();
                // 数据库资源地址
                String downloadPath = localPath + FileUtils.stripPrefix(url);
                // 下载名称
                String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                FileUtils.setAttachmentResponseHeader(response, downloadName);
                FileUtils.writeBytes(downloadPath, response.getOutputStream());
            }
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }

        // 优先缓存读取
        byte[] imgData=ImageUtils.getCacheImage(url);

        //缓存没有数据,尝试缓存
        if (imgData==null){
            if (ImageUtils.cacheImage(url)){
                //缓存成功，重新读取并返回数据
                imgData=ImageUtils.getCacheImage(url);
            }else{
                // 缓存失败，返回500
                return ResponseEntity.internalServerError().build();
            }
        }

        return new ResponseEntity<>(imgData, ImageUtils.getImgHeaders(url, imgData.length), HttpStatus.OK);
    }
}
