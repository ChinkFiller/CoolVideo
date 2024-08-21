package com.test.http_spring.Controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


//这是内置的CDN视频解析类，用于返回服务器储存的数据
@Controller
@RequestMapping("/videos")
public class VideoController {
    private static final String VIDEO_PATH = "video/";

    private static String MD5(String input) {
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
    //为防止他人盗链下载，默认本地节点仅支持流媒体模式，不支持mp4下载模式
    @GetMapping(value = "/get_film", produces = "video/mp4")
    public ResponseEntity<InputStreamResource> streamVideo(@RequestParam("id") String id, @RequestParam("num") String num, @RequestParam("sign") String sign,
                                                           @RequestHeader(value = "Range", required = false) String rangeHeader){
        try {
            String code = MD5("AADM@conyafertools" + id + "@" + num + (System.currentTimeMillis() / 1000) / 18000);
            if (!sign.equals(code)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            File videoFile = new File(VIDEO_PATH + id + "/" + num + ".mp4");
            if (!videoFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            long fileLength = videoFile.length();
            long start = 0;
            long end = fileLength - 1;

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                try {
                    start = Long.parseLong(ranges[0]);
                    if (ranges.length > 1) {
                        end = Long.parseLong(ranges[1]);
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).body(null);
                }
            }

            if (start > end || end >= fileLength) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).body(null);
            }

            long contentLength = end - start + 1;
            InputStream inputStream = new FileInputStream(videoFile);
            inputStream.skip(start);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("video/mp4"));
            headers.setContentLength(contentLength);
            headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            headers.add("Accept-Ranges", "bytes");

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(new InputStreamResource(inputStream) {
                        @Override
                        public long contentLength() {
                            return contentLength;
                        }
                    });
        }catch (IOException e1){
            return ResponseEntity.status(200).body(null);
        }
    }
}
