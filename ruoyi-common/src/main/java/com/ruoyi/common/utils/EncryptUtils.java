package com.ruoyi.common.utils;

import com.ruoyi.common.utils.sign.Md5Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

/**
 * 加密工具类
 */
public class EncryptUtils {


    /**
     * 解密API密钥
     * @param encryptedData 加密的base64数据
     * @return 解密后的API密钥
     */
    public String decryptAES(String encryptedData,String key) throws Exception {
        // 将Base64编码的加密数据解析出来
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

        if (key.length()!=16){
            throw new Exception("密钥长度不正确");
        }

        //加载密钥为byte数组
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = "oTxsJyHv32e0t219".getBytes(StandardCharsets.UTF_8);

        // 确保密钥长度为16字节（128位）
        keyBytes = Arrays.copyOf(keyBytes, 16);
        ivBytes = Arrays.copyOf(ivBytes, 16);

        // 创建AES密钥规范
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // 创建IV参数规范
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        // 创建密码器实例
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // 初始化密码器为解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        // 执行解密操作
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // 将解密后的字节数组转换为字符串
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 加密Api密钥数据
     * @param data 数据字符串
     * @return 加密后的字符串
     */
    public String encryptAES(String data,String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        if (key.length()!=16){
            throw new Exception("密钥长度不正确");
        }
        byte[] keyBytes = Arrays.copyOf(key.getBytes(), 16);
        byte[] ivBytes = Arrays.copyOf("oTxsJyHv32e0t219".getBytes(), 16);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }
    /**
     * 加密登录信息使用
     * @author Conyafer
     * @param data 需要加密的字符串
     * @return 加密字符串
     */
    public String encryptMsg(String data) {
        String key = TimeUtils.getNowFormatDateYMD();
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

        return Base64.getEncoder().encodeToString(last.toString().getBytes());
    }

    /**
     * 解密登录信息使用
     * @author Conyafer
     * @param data 加密信息
     * @return 解密后的字符串
     */
    public String decryptMsg(String data) {
        String key = TimeUtils.getNowFormatDateYMD();
        byte[] decodedBytes = Base64.getDecoder().decode(data);
        data = new String(decodedBytes);
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



    public static String generateRandomCode(){
        Random r=new Random();
        r.setSeed(new Date().getTime()+71485);
        return String.valueOf(r.nextInt(900000)+100000);
    }


    /**
     * 验证播放链接的签名
     * @param id 视频的id
     * @param part 视频的分集
     * @param providedSig 签名
     * @return 签名是否正确
     */
    public static boolean verifyPlayerSign(String id, String part, String providedSig) {
        // 生成签名摘要信息
        String expected = Md5Utils.hash(id + "-" + part + "-" + System.currentTimeMillis()/1000000 + "-" + "uxsasxwf");
        // 验证签名
        return expected.equals(providedSig);
    }
}
