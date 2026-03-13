package com.ruoyi.video.admin.domain.form;

import lombok.Data;

@Data
public class VideoStorageUploadForm {
    Long id;
    // 视频ID
    Long vid;
    // 视频分集
    Integer part;
    // 上传文件的md5值
    String fileMd5;
    // 上传文件的文件名称
    String fileName;
    // 上传文件的区块数量
    Integer chunkCount;
}
