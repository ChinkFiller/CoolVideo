package com.ruoyi.web.controller.common;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.ISysChunkUploadService;
import com.ruoyi.system.service.ISysOssFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/oss/file/upload/chunk")
public class OssFileUploadController extends BaseController {

    @Autowired
    ISysOssFileService ossFileService;

    @Autowired
    ISysChunkUploadService chunkUploadService;

    private static final String UPLOAD_DIR = "upload-temp/";


    /**
     * 检查文件是否上传完成
     */
    @GetMapping("/check")
    public AjaxResult check(@RequestParam("fileMd5") String fileMd5) {

        File chunkDir = new File(UPLOAD_DIR + fileMd5);
        Set<Integer> uploaded = new HashSet<>();

        if (chunkDir.exists()) {
            for (File f : chunkDir.listFiles()) {
                String name = f.getName().replace(".part", "");
                uploaded.add(Integer.valueOf(name));
            }
        }


        return success(Map.of("uploaded", uploaded));
    }

    /**
     * 保存分片
     */
    @PostMapping("/chunk")
    public AjaxResult uploadChunk(@RequestParam("file") MultipartFile file,
                                  @RequestParam("fileMd5") String fileMd5,
                                  @RequestParam("chunkIndex") Integer chunkIndex) throws Exception {

        File chunkDir = new File(UPLOAD_DIR + fileMd5);
        if (!chunkDir.exists()) {
            chunkDir.mkdirs();
        }

        // 创建区块文件
        File chunkFile = new File(UPLOAD_DIR+fileMd5+"/"+chunkIndex + ".part");

        try (InputStream in = file.getInputStream();
             OutputStream out = new FileOutputStream(chunkFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();

            return success();

        } catch (Exception e) {
            e.printStackTrace();
            return error("文件保存失败：" + e.getMessage());
        }
    }

    /**
     * 合并分片至Oss存储
     */
    @PostMapping("/merge")
    public AjaxResult mergeToOss(
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkCount") Integer chunkCount
    ) throws Exception {
        return success(Map.of("fileID",chunkUploadService.mergeChunkToOss(fileMd5, fileName, chunkCount)));
    }
}
