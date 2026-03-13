package com.ruoyi.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImgProcessUtils {

    /**
     * 压缩图片并转化为base64编码的函数，有配置文件配置大小
     * @param imageData 图片字节数组
     * @return base64编码的字符串
     */
    public static String compressAndEncodeImage(byte[] imageData,int targetWidth,int targetHeight) throws IOException {
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
}
