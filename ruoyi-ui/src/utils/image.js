export function getVideoFileCover(file,callback) {
  const url=URL.createObjectURL(file)
  getVideoCover(url,(img)=>{
    URL.revokeObjectURL(url)
    callback(img)
  })
}
export function getWebVideoCover(url,callback){
  getVideoCover(url,callback)
}


function getVideoCover(url,callback){
  const canvas=document.createElement('canvas')
  const video=document.createElement('video')

  video.src = url;
  video.crossOrigin = 'anonymous'; // 如果是跨域视频，必须加上这一行
  video.load();

  // 等待视频元数据加载完成
  video.addEventListener('loadedmetadata', function() {
    // 跳到视频的第1秒（可修改为任意时间点）
    video.currentTime = Math.min(1, video.duration / 2);
  });

  // 当指定时间的视频帧加载完成后截取
  video.addEventListener('seeked', function() {
    const ctx = canvas.getContext('2d');

    // 设置canvas大小与视频相同
    canvas.width = 150;
    canvas.height = 150;

    // 绘制当前帧
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

    // 转换为图片URL
    const imgUrl = canvas.toDataURL('image/jpeg',0.8);

    callback(imgUrl)
  });
}
