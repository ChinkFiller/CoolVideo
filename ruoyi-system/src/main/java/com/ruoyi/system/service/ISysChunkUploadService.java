package com.ruoyi.system.service;

import java.io.IOException;

public interface ISysChunkUploadService {
    String mergeChunkToOss(String fileMd5, String fileName, Integer chunkCount) throws IOException;

    String mergeChunkToLocal(String fileMd5, String fileName, Integer chunkCount) throws IOException;
}
