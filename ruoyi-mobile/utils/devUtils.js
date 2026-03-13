export function saveRequest(data){
    const orData = uni.getStorageSync('REQUEST_LOG');
    if(orData.length>50){orData.shift();}
    orData.push(data);
    uni.setStorageSync('REQUEST_LOG',orData);
}
