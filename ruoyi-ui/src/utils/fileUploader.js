import SparkMD5 from "spark-md5";
import axios from "axios";
import {getToken} from "@/utils/auth";

const chunkSizeMB = 20 // 分块大小，单位MB
const header = {
  'Authorization': 'Bearer ' + getToken()
}

/** 计算文件的md5 **/
async function getFileMd5(file) {
  return new Promise((resolve, reject) => {
    const chunkSize = chunkSizeMB * 1024 * 1024;
    const spark = new SparkMD5.ArrayBuffer();
    const fileReader = new FileReader();
    let offset = 0;

    fileReader.onload = e => {
      spark.append(e.target.result);

      offset += chunkSize;

      if (offset < file.size) {
        loadNext();
      } else {
        resolve(spark.end());
      }
    };

    fileReader.onerror = reject;

    function loadNext() {
      const slice = file.slice(offset, offset + chunkSize);
      fileReader.readAsArrayBuffer(slice);
    }

    loadNext();
  });
}

async function uploadChunks(file, fileMd5,config) {
  const chunkSize = chunkSizeMB * 1024 * 1024;
  const chunkCount = Math.ceil(file.size / chunkSize);
  const serverBaseUrl = process.env.VUE_APP_BASE_API + "/oss/file/upload/chunk"
  let totalProcess=0;
  let reTryCount=0;
  let chunkRes;

  // 查询已上传的分片
  chunkRes = await axios.get(serverBaseUrl+"/check", {
    params: { fileMd5 },
    headers:header
  });
  if (chunkRes.data.code !== 200) {
    config.error(new Error(chunkRes.data.msg))
    return
  }



  for (let i = 0; i < chunkCount; i++) {
    if (chunkRes.data.data.uploaded.includes(i)) {
      console.log(`跳过分片 ${i}`);
      continue;
    }

    let chunk = file.slice(i * chunkSize, (i + 1) * chunkSize);
    let form = new FormData();
    form.append("file", chunk);
    form.append("fileMd5", fileMd5);
    form.append("chunkIndex", i);


    while (reTryCount === 0) {
      const chunkUploadRes=await axios.post(serverBaseUrl+"/chunk", form, {
        onUploadProgress: e => {
          const percent = ((i / chunkCount) * 100 + (e.loaded / e.total) * (100 / chunkCount)).toFixed(2);
          config.process(percent)
        },
        headers:Object.assign(header,{
          'Content-Type': 'multipart/form-data',
        })
      });
      if (chunkUploadRes.data.code!==200){
        // 上传三次失败，返回错误信息
        if (reTryCount >= 3){
          config.error(new Error(chunkRes.data.msg))
          break
        }
        // 重试次数+1
        reTryCount+=1;
      }else{
        // 重试次数复位
        reTryCount=0;
        break
      }
    }

    // 重试次数不为0，说明超过了重试的次数，退出区块上传
    if (reTryCount !== 0){
      return
    }

  }

  // 使用默认的分片合并
  if (!config.disableAutoMerge){
    // 所有分片上传完成 → 发送合并请求
        const res=await axios.post(serverBaseUrl+"/merge", {
          fileMd5,
          fileName: file.name,
          chunkCount
        },{
          headers:Object.assign(header,{
            'Content-Type': 'multipart/form-data',
          })
        });
        if (res.data.code !== 200) {
          config.error(new Error(res.data.msg))
        }else{
          config.success(res.data.data.fileID)
        }
  }else{
    // 手动合并
    config.merge({
      fileMd5:fileMd5,
      fileName: file.name,
      chunkCount:chunkCount
    })
  }

}

export async function uploadFileToOssByChunk(file,config){
  try {
    const md5 = await getFileMd5(file);
    await uploadChunks(file, md5 ,config);
  }catch (e){
    config.error(e)
  }
}
