package com.ruoyi.system.service.impl;


import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.system.service.ISysChunkUploadService;
import com.ruoyi.system.service.ISysOssFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;

@Service
public class SysChunkCountServiceImpl implements ISysChunkUploadService {
    @Autowired
    ISysOssFileService ossFileService;

    @Value("${ruoyi.profile}")
    String uploadPath;

    private static final String UPLOAD_DIR = "upload-temp/";

    private void mergeParts(Integer chunkCount, File chunkDir, File finalFile) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(finalFile))) {

            for (int i = 0; i < chunkCount; i++) {

                File chunkFile = new File(chunkDir, i + ".part");

                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(chunkFile))) {
                    byte[] buffer = new byte[1024 * 1024];
                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                }
            }
        }
    }

    @Override
    public String mergeChunkToOss(String fileMd5, String fileName, Integer chunkCount) throws IOException{
        File chunkDir = new File(UPLOAD_DIR + fileMd5);
        File finalFile = File.createTempFile("tempart-",fileName);

        String ossFileName;
        try {
            mergeParts(chunkCount, chunkDir, finalFile);

            ossFileName= UUID.getUUIDWithOutLine() + Files.probeContentType(finalFile.toPath()).replace("/",".");

            ossFileService.uploadFile(finalFile,null,ossFileName);

            // 删除分片
            for (File f : chunkDir.listFiles()) f.delete();

            // 删除分片目录
            chunkDir.delete();

            // 删除临时文件
            finalFile.delete();
        } catch (Exception e){
            // 删除临时文件
            finalFile.delete();
            throw new IOException(e);
        }

        // 返回oss的文件id
        return ossFileName;
    }



    @Override
    public String mergeChunkToLocal(String fileMd5, String fileName, Integer chunkCount) throws IOException {
        File chunkDir = new File(UPLOAD_DIR + fileMd5);
        File finalFile = new File(uploadPath+fileName);

        mergeParts(chunkCount, chunkDir, finalFile);

        // 删除分片
        for (File f : chunkDir.listFiles()) f.delete();

        // 删除分片目录
        chunkDir.delete();

        // 返回文件的路径
        return "/profile/"+fileName;
    }
}
